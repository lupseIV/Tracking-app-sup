package com.lupseiv.supplementtracker.data

/**
 * Built-in supplement catalog shipped with the app. Benefits are general,
 * commonly cited effects and are informational only — not medical advice.
 */
object SeedData {

    data class SeedSupplement(
        val supplement: Supplement,
        val searchTerm: String,
    )

    /** Standard store options generated for every catalog supplement. */
    fun buyOptionsFor(supplementId: Long, searchTerm: String): List<BuyOption> {
        val encoded = java.net.URLEncoder.encode(searchTerm, "UTF-8")
        return listOf(
            BuyOption(supplementId = supplementId, storeName = "Amazon", url = "https://www.amazon.com/s?k=$encoded"),
            BuyOption(supplementId = supplementId, storeName = "iHerb", url = "https://www.iherb.com/search?kw=$encoded"),
            BuyOption(supplementId = supplementId, storeName = "eMAG", url = "https://www.emag.ro/search/$encoded"),
        )
    }

    val catalog: List<SeedSupplement> = listOf(
        seed(
            name = "Vitamin D3",
            category = "Vitamin",
            description = "The 'sunshine vitamin', commonly supplemented in months with little sun exposure.",
            dosage = "1000–4000 IU daily, with a meal containing fat",
            benefits = listOf(
                "Supports bone health and calcium absorption",
                "Supports immune system function",
                "May improve mood, especially in winter",
            ),
            searchTerm = "vitamin d3",
        ),
        seed(
            name = "Vitamin C",
            category = "Vitamin",
            description = "A water-soluble antioxidant vitamin found in fruits and vegetables.",
            dosage = "250–1000 mg daily",
            benefits = listOf(
                "Supports immune defense",
                "Antioxidant protection against free radicals",
                "Aids collagen production and iron absorption",
            ),
            searchTerm = "vitamin c",
        ),
        seed(
            name = "Omega-3 (Fish Oil)",
            category = "Fatty acid",
            description = "EPA and DHA fatty acids, usually sourced from fish or algae oil.",
            dosage = "1–2 g combined EPA+DHA daily, with food",
            benefits = listOf(
                "Supports heart and cardiovascular health",
                "Supports brain function and memory",
                "Helps reduce inflammation and supports joint health",
            ),
            searchTerm = "omega 3 fish oil",
        ),
        seed(
            name = "Magnesium",
            category = "Mineral",
            description = "An essential mineral involved in over 300 enzymatic reactions; glycinate and citrate forms are well absorbed.",
            dosage = "200–400 mg daily, often in the evening",
            benefits = listOf(
                "Supports muscle relaxation and recovery",
                "May improve sleep quality",
                "Supports nervous system and energy metabolism",
            ),
            searchTerm = "magnesium glycinate",
        ),
        seed(
            name = "Zinc",
            category = "Mineral",
            description = "A trace mineral important for immunity, skin, and hormone production.",
            dosage = "10–25 mg daily, with food",
            benefits = listOf(
                "Supports immune function",
                "Supports skin health and wound healing",
                "Contributes to normal testosterone levels",
            ),
            searchTerm = "zinc picolinate",
        ),
        seed(
            name = "Creatine Monohydrate",
            category = "Performance",
            description = "One of the most researched sports supplements, stored in muscle as phosphocreatine.",
            dosage = "3–5 g daily, any time of day",
            benefits = listOf(
                "Increases strength and power output",
                "Supports muscle growth alongside training",
                "May support cognitive performance",
            ),
            searchTerm = "creatine monohydrate",
        ),
        seed(
            name = "Whey Protein",
            category = "Protein",
            description = "Fast-digesting dairy protein used to hit daily protein targets.",
            dosage = "20–40 g per serving, as needed to meet protein goals",
            benefits = listOf(
                "Supports muscle repair and growth",
                "Convenient way to increase daily protein intake",
                "Rich in essential amino acids (high leucine)",
            ),
            searchTerm = "whey protein",
        ),
        seed(
            name = "Probiotics",
            category = "Gut health",
            description = "Live beneficial bacteria, often Lactobacillus and Bifidobacterium strains.",
            dosage = "1–10 billion CFU daily, per product label",
            benefits = listOf(
                "Supports gut flora balance and digestion",
                "May reduce bloating and digestive discomfort",
                "Supports immune health via the gut",
            ),
            searchTerm = "probiotics",
        ),
        seed(
            name = "Vitamin B12",
            category = "Vitamin",
            description = "Essential vitamin mainly found in animal products; important for vegans and vegetarians.",
            dosage = "250–1000 mcg daily (methylcobalamin or cyanocobalamin)",
            benefits = listOf(
                "Supports energy metabolism and reduces fatigue",
                "Supports red blood cell formation",
                "Supports nerve function",
            ),
            searchTerm = "vitamin b12",
        ),
        seed(
            name = "Multivitamin",
            category = "Vitamin",
            description = "Broad-spectrum blend of vitamins and minerals as nutritional insurance.",
            dosage = "1 serving daily, with food",
            benefits = listOf(
                "Covers common micronutrient gaps in the diet",
                "Convenient all-in-one formula",
                "Supports overall health and energy",
            ),
            searchTerm = "multivitamin",
        ),
        seed(
            name = "Ashwagandha",
            category = "Herbal / Adaptogen",
            description = "Adaptogenic herb from Ayurvedic tradition, typically standardized as KSM-66 or Sensoril.",
            dosage = "300–600 mg daily of root extract",
            benefits = listOf(
                "May reduce stress and cortisol levels",
                "May improve sleep quality",
                "May support strength and recovery",
            ),
            searchTerm = "ashwagandha",
        ),
        seed(
            name = "Melatonin",
            category = "Sleep",
            description = "Hormone that regulates the sleep–wake cycle; used short-term for sleep onset and jet lag.",
            dosage = "0.5–3 mg, 30–60 minutes before bed",
            benefits = listOf(
                "Helps fall asleep faster",
                "Useful for jet lag and shift work",
                "Supports regulation of circadian rhythm",
            ),
            searchTerm = "melatonin",
        ),
        seed(
            name = "Iron",
            category = "Mineral",
            description = "Essential mineral for oxygen transport; supplement only if intake or levels are low.",
            dosage = "14–27 mg daily, ideally with vitamin C; check levels first",
            benefits = listOf(
                "Supports red blood cell and hemoglobin production",
                "Helps reduce tiredness caused by low iron",
                "Important for menstruating people and endurance athletes",
            ),
            searchTerm = "iron supplement",
        ),
        seed(
            name = "Calcium",
            category = "Mineral",
            description = "The most abundant mineral in the body, mostly stored in bones and teeth.",
            dosage = "500–1000 mg daily if dietary intake is low",
            benefits = listOf(
                "Supports bone and teeth strength",
                "Supports muscle contraction and nerve signaling",
                "Works together with vitamin D3 and K2",
            ),
            searchTerm = "calcium supplement",
        ),
        seed(
            name = "Collagen Peptides",
            category = "Protein",
            description = "Hydrolyzed collagen protein, typically types I and III from bovine or marine sources.",
            dosage = "5–15 g daily, mixes into drinks",
            benefits = listOf(
                "Supports skin elasticity and hydration",
                "Supports joint, tendon, and ligament health",
                "Supports hair and nail strength",
            ),
            searchTerm = "collagen peptides",
        ),
        seed(
            name = "Curcumin (Turmeric)",
            category = "Herbal",
            description = "Active compound in turmeric; usually paired with piperine or a lipid formula for absorption.",
            dosage = "500–1000 mg daily with black pepper extract",
            benefits = listOf(
                "Helps reduce inflammation",
                "Supports joint comfort and mobility",
                "Antioxidant support",
            ),
            searchTerm = "curcumin turmeric",
        ),
        seed(
            name = "CoQ10",
            category = "Antioxidant",
            description = "Coenzyme Q10, involved in cellular energy production; levels decline with age and statin use.",
            dosage = "100–200 mg daily, with a fatty meal",
            benefits = listOf(
                "Supports cellular energy production",
                "Supports heart health",
                "Antioxidant protection",
            ),
            searchTerm = "coq10",
        ),
        seed(
            name = "Electrolytes",
            category = "Hydration",
            description = "Sodium, potassium, and magnesium blend for hydration during sweat-heavy activity.",
            dosage = "1 serving during/after intense exercise or heat",
            benefits = listOf(
                "Supports hydration and fluid balance",
                "Helps prevent muscle cramps",
                "Replaces minerals lost through sweat",
            ),
            searchTerm = "electrolytes powder",
        ),
    )

    private fun seed(
        name: String,
        category: String,
        description: String,
        dosage: String,
        benefits: List<String>,
        searchTerm: String,
    ) = SeedSupplement(
        supplement = Supplement(
            name = name,
            category = category,
            description = description,
            dosage = dosage,
            benefits = benefits,
        ),
        searchTerm = searchTerm,
    )
}
