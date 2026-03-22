package com.babyfeeding.entity;

import com.babyfeeding.enums.Acceptance;
import com.babyfeeding.enums.FeedingType;
import com.babyfeeding.enums.FoodAmount;
import com.babyfeeding.enums.FoodType;
import com.babyfeeding.enums.MilkType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "feeding_records")
public class FeedingRecord {
    @Id
    private String id;
    private String babyId;
    private FeedingType type;
    private Long timestamp;
    private Integer amountMl;
    private MilkType milkType;
    private FoodType foodType;
    private FoodAmount foodAmount;
    private Acceptance acceptance;
    private String note;
    private String createdBy;
    private Long createdAt;
    private Long updatedAt;
    private Boolean deleted = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBabyId() {
        return babyId;
    }

    public void setBabyId(String babyId) {
        this.babyId = babyId;
    }

    public FeedingType getType() {
        return type;
    }

    public void setType(FeedingType type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getAmountMl() {
        return amountMl;
    }

    public void setAmountMl(Integer amountMl) {
        this.amountMl = amountMl;
    }

    public MilkType getMilkType() {
        return milkType;
    }

    public void setMilkType(MilkType milkType) {
        this.milkType = milkType;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }

    public FoodAmount getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(FoodAmount foodAmount) {
        this.foodAmount = foodAmount;
    }

    public Acceptance getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(Acceptance acceptance) {
        this.acceptance = acceptance;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
