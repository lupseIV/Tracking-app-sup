// Electron main process: starts the bundled Spring Boot backend as a child
// process, waits for its health endpoint, then opens the app window pointed
// at the backend (which also serves the Angular build as static content).
const { app, BrowserWindow, dialog } = require('electron');
const { spawn } = require('child_process');
const http = require('http');
const path = require('path');
const fs = require('fs');

const PORT = 8090;
const HEALTH_URL = `http://127.0.0.1:${PORT}/actuator/health`;
const APP_URL = `http://127.0.0.1:${PORT}/`;

let backendProcess = null;

function resourcePath(...segments) {
  // In development resources live next to this file; in a packaged app they
  // are under process.resourcesPath (see "extraResources" in package.json).
  const base = app.isPackaged ? process.resourcesPath : path.join(__dirname, '..');
  return path.join(base, ...segments);
}

function findJava() {
  // Prefer the jlink runtime bundled with the app, fall back to system java.
  const bundled = resourcePath('jre', 'bin', process.platform === 'win32' ? 'java.exe' : 'java');
  return fs.existsSync(bundled) ? bundled : 'java';
}

function findBackendJar() {
  if (app.isPackaged) {
    return resourcePath('backend', 'backend.jar');
  }
  return path.join(__dirname, '..', 'backend', 'target', 'supplement-tracker-backend-0.1.0.jar');
}

function startBackend() {
  const jar = findBackendJar();
  if (!fs.existsSync(jar)) {
    dialog.showErrorBox(
      'Backend not found',
      `Could not find the backend jar at:\n${jar}\n\nRun "mvn package" in backend/ first.`,
    );
    app.quit();
    return;
  }
  backendProcess = spawn(findJava(), ['-jar', jar], {
    env: {
      ...process.env,
      SUPPLEMENT_PORT: String(PORT),
      SUPPLEMENT_DATA_DIR: app.getPath('userData'),
    },
    stdio: 'ignore',
  });
  backendProcess.on('exit', (code) => {
    backendProcess = null;
    if (code !== 0 && code !== null) {
      dialog.showErrorBox('Backend stopped', `The local backend exited with code ${code}.`);
      app.quit();
    }
  });
}

function waitForBackend(retriesLeft, onReady) {
  const request = http.get(HEALTH_URL, (response) => {
    response.resume();
    if (response.statusCode === 200) {
      onReady();
    } else {
      retry(retriesLeft, onReady);
    }
  });
  request.on('error', () => retry(retriesLeft, onReady));
  request.setTimeout(1000, () => request.destroy());
}

function retry(retriesLeft, onReady) {
  if (retriesLeft <= 0) {
    dialog.showErrorBox('Startup failed', 'The local backend did not start in time.');
    app.quit();
    return;
  }
  setTimeout(() => waitForBackend(retriesLeft - 1, onReady), 500);
}

function createWindow() {
  const window = new BrowserWindow({
    width: 1100,
    height: 800,
    title: 'Supplement Tracker',
    webPreferences: {
      contextIsolation: true,
      nodeIntegration: false,
    },
  });
  window.loadURL(APP_URL);
}

app.whenReady().then(() => {
  startBackend();
  waitForBackend(60, createWindow);

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('quit', () => {
  if (backendProcess) {
    backendProcess.kill();
  }
});
