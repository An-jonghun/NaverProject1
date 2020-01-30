package com.example.vedioplaytest.PoseAnalysis;

import java.util.ArrayList;
import java.util.List;

public class UserJSON {
    private double shoulderLeftX;
    private double shoulderLeftY;
    private double shoulderRightX;
    private double shoulderRightY;

    private double elbowLeftX;
    private double elbowLeftY;
    private double elbowRightX;
    private double elbowRightY;

    private double wristLeftX;
    private double wristLeftY;
    private double wristRightX;
    private double wristRightY;

    private GetObjectJSON getData;

    private List<Double> dataSet = new ArrayList<>();

    public UserJSON(String json) {
        this.getData = new GetObjectJSON(json);
        setData();
    }

    public List<Double> getData() {
        dataSet.add(getShoulderLeftX());
        dataSet.add(getShoulderLeftY());
        dataSet.add(getShoulderRightX());
        dataSet.add(getShoulderRightY());

        dataSet.add(getElbowLeftX());
        dataSet.add(getElbowLeftY());
        dataSet.add(getElbowRightX());
        dataSet.add(getElbowRightY());

        dataSet.add(getWristLeftX());
        dataSet.add(getWristLeftY());
        dataSet.add(getWristRightX());
        dataSet.add(getWristRightY());

        return dataSet;
    }

    private void setData() {
        this.shoulderLeftX = getData.exportData("5", "x");
        this.shoulderLeftY = getData.exportData("5", "y");
        this.shoulderRightX = getData.exportData("2", "x");
        this.shoulderRightY = getData.exportData("2", "y");
        this.elbowLeftX = getData.exportData("6", "x");
        this.elbowLeftY = getData.exportData("6", "y");
        this.elbowRightX = getData.exportData("3", "x");
        this.elbowRightY = getData.exportData("3", "y");
        this.wristLeftX = getData.exportData("7", "x");
        this.wristLeftY = getData.exportData("7", "y");
        this.wristRightX = getData.exportData("4", "x");
        this.wristRightY = getData.exportData("4", "y");
    }

    public double getShoulderLeftX() {
        return shoulderLeftX;
    }
    public double getShoulderLeftY() {
        return shoulderLeftY;
    }
    public double getShoulderRightX() {
        return shoulderRightX;
    }
    public double getShoulderRightY() {
        return shoulderRightY;
    }
    public double getElbowLeftX() {
        return elbowLeftX;
    }
    public double getElbowLeftY() {
        return elbowLeftY;
    }
    public double getElbowRightX() {
        return elbowRightX;
    }
    public double getElbowRightY() {
        return elbowRightY;
    }
    public double getWristLeftX() {
        return wristLeftX;
    }
    public double getWristLeftY() {
        return wristLeftY;
    }
    public double getWristRightX() {
        return wristRightX;
    }
    public double getWristRightY() {
        return wristRightY;
    }
}
