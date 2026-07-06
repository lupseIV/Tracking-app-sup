package com.lupseiv.supplements.supplement;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "supplement")
public class Supplement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "typical_dosage", nullable = false)
    private String typicalDosage;

    @Column(nullable = false, length = 60)
    private String category;

    @Column(name = "is_custom", nullable = false)
    private boolean custom;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "supplement_benefit", joinColumns = @JoinColumn(name = "supplement_id"))
    @OrderColumn(name = "position")
    @Column(name = "benefit", nullable = false)
    private List<String> benefits = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "supplement_buy_link", joinColumns = @JoinColumn(name = "supplement_id"))
    @OrderColumn(name = "position")
    private List<BuyLink> buyLinks = new ArrayList<>();

    protected Supplement() {
        // for JPA
    }

    public Supplement(String name, String description, String typicalDosage, String category,
                      boolean custom, List<String> benefits, List<BuyLink> buyLinks) {
        this.name = name;
        this.description = description;
        this.typicalDosage = typicalDosage;
        this.category = category;
        this.custom = custom;
        this.benefits = new ArrayList<>(benefits);
        this.buyLinks = new ArrayList<>(buyLinks);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getTypicalDosage() {
        return typicalDosage;
    }

    public String getCategory() {
        return category;
    }

    public boolean isCustom() {
        return custom;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public List<BuyLink> getBuyLinks() {
        return buyLinks;
    }
}
