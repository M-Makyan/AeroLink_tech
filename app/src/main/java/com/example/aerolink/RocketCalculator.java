package com.example.aerolink;

public class RocketCalculator {

    public static double calculateDeltaV(double exhaustVelocity, double initialMass, double finalMass) {
        return exhaustVelocity * Math.log(initialMass / finalMass);
    }

    public static double calculateSpecificImpulse(double thrust, double massFlowRate) {
        return thrust / (massFlowRate * 9.81); // 9.81 m/s² is Earth's gravity
    }

    public static double calculateThrust(double exhaustVelocity, double massFlowRate) {
        return exhaustVelocity * massFlowRate;
    }
}