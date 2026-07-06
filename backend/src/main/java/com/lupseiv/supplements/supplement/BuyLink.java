package com.lupseiv.supplements.supplement;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BuyLink {

    @Column(name = "store_name", nullable = false, length = 60)
    private String storeName;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    protected BuyLink() {
        // for JPA
    }

    public BuyLink(String storeName, String url) {
        this.storeName = storeName;
        this.url = url;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getUrl() {
        return url;
    }
}
