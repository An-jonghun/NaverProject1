package com.example.vedioplaytest.PoseAnalysis;

import android.util.Log;

import java.util.List;

public class PoseAnalysis {
    private UserJSON user;

// JSON 받아서 저장
//    private String userString;

    // 1 = 올리는 자세
    // 0 = 내리는 자세

    public PoseAnalysis(String jsonData, int status) {

//        user = new UserJSON(userString);
        user = new UserJSON(jsonData);

        if (status == 1) { // 올리는 자세
            checkPress(true);
        } else if (status == 0) { // 내리는 자세
            checkPress(false);
        }
    }

    private void checkPress(boolean isPressUp) {
        double compareResult = compare();

        if (isPressUp) { // 올리는 자세
            if (compareResult > 1.2) {
                Log.d("결과", "올리기가 잘못 되었습니다 " + compareResult);
            } else {
                Log.d("결과", "올리기가 정확합니다 " + compareResult);
            }

        } else { // 내리는 자세
            if (compareResult < 1.6) {
                Log.d("결과", "내리기가 잘못 되었습니다 " + compareResult);
            } else {
                Log.d("결과", "내리기가 정확합니다 " + compareResult);
            }
        }
    }

    // 0 ~ 3 어깨 좌표
    // 4 ~ 7 팔꿈치 좌표
    // 8 ~ 11 손목 좌표
    private double compare() {
        List<Double> userData = user.getData();

        double userAngleL = protractor(userData, false);
        double userAngleR = protractor(userData, true);

        double userResult = (userAngleL + userAngleR) / 2;

        return userResult;
    }

    private double protractor(List<Double> data, boolean isRight) {
        int alpha = 0; // alpha : 0 = 왼쪽 2 = 오른쪽
        if (isRight) alpha = 2;

        double FrontX = data.get(8 + alpha) - data.get(4 + alpha);
        double FrontY = data.get(9 + alpha) - data.get(5 + alpha);
        double BackX = data.get(4 + alpha) - data.get(0 + alpha);
        double BackY = data.get(5 + alpha) - data.get(1 + alpha);

        return cosin(FrontX, FrontY, BackX, BackY);
    }

    private double cosin(double x1, double y1, double x2, double y2) {
        double A = product(x1, y1, x2, y2);
        double B = scale(x1, y1);
        double C = scale(x2, y2);

        double result = Math.acos(A / (B * C));

        result = Math.round(result * 100) / 100.0;

        return result;
    }

    private double product(double x1, double y1, double x2, double y2) {
        double A = x1 * y1;
        double B = x2 * y2;
        return A + B;
    }

    private double scale(double X, double Y) {
        return Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
    }
}