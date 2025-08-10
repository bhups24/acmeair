package com.acmeair.dto;

public class SeatClasses {
    private SeatInfo economy;
    private SeatInfo premiumEconomy;
    private SeatInfo business;
    private SeatInfo firstClass;

    public SeatClasses() {}

    public SeatClasses(SeatInfo economy, SeatInfo premiumEconomy, SeatInfo business, SeatInfo firstClass) {
        this.economy = economy;
        this.premiumEconomy = premiumEconomy;
        this.business = business;
        this.firstClass = firstClass;
    }

    public SeatInfo getEconomy() { return economy; }
    public void setEconomy(SeatInfo economy) { this.economy = economy; }

    public SeatInfo getPremiumEconomy() { return premiumEconomy; }
    public void setPremiumEconomy(SeatInfo premiumEconomy) { this.premiumEconomy = premiumEconomy; }

    public SeatInfo getBusiness() { return business; }
    public void setBusiness(SeatInfo business) { this.business = business; }

    public SeatInfo getFirstClass() { return firstClass; }
    public void setFirstClass(SeatInfo firstClass) { this.firstClass = firstClass; }
}