-- Adds a "Google Shopping" buy link to every built-in supplement (roadmap:
-- shopping-search option 1). Google Shopping aggregates products from many
-- producers and shops in one result list; the link opens in the system browser
-- like the other stores (UC-04). Position 3 keeps it after Amazon/iHerb/eMAG.

INSERT INTO supplement_buy_link (supplement_id, store_name, url, position) VALUES
(1,  'Google Shopping', 'https://www.google.com/search?tbm=shop&q=vitamin+d3', 3),
(2,  'Google Shopping', 'https://www.google.com/search?tbm=shop&q=vitamin+c', 3),
(3,  'Google Shopping', 'https://www.google.com/search?tbm=shop&q=vitamin+b12', 3),
(4,  'Google Shopping', 'https://www.google.com/search?tbm=shop&q=omega+3+fish+oil', 3),
(5,  'Google Shopping', 'https://www.google.com/search?tbm=shop&q=magnesium+glycinate', 3),
(6,  'Google Shopping', 'https://www.google.com/search?tbm=shop&q=zinc+picolinate', 3),
(7,  'Google Shopping', 'https://www.google.com/search?tbm=shop&q=creatine+monohydrate', 3),
(8,  'Google Shopping', 'https://www.google.com/search?tbm=shop&q=whey+protein', 3),
(9,  'Google Shopping', 'https://www.google.com/search?tbm=shop&q=probiotics', 3),
(10, 'Google Shopping', 'https://www.google.com/search?tbm=shop&q=ashwagandha', 3),
(11, 'Google Shopping', 'https://www.google.com/search?tbm=shop&q=melatonin', 3),
(12, 'Google Shopping', 'https://www.google.com/search?tbm=shop&q=iron+supplement', 3),
(13, 'Google Shopping', 'https://www.google.com/search?tbm=shop&q=calcium+supplement', 3),
(14, 'Google Shopping', 'https://www.google.com/search?tbm=shop&q=collagen+peptides', 3),
(15, 'Google Shopping', 'https://www.google.com/search?tbm=shop&q=curcumin+turmeric', 3),
(16, 'Google Shopping', 'https://www.google.com/search?tbm=shop&q=coq10', 3),
(17, 'Google Shopping', 'https://www.google.com/search?tbm=shop&q=electrolytes+powder', 3),
(18, 'Google Shopping', 'https://www.google.com/search?tbm=shop&q=multivitamin', 3);
