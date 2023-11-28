package com.example.aucation.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RangeValueCalculator {

    public static int calculateValue(int input) {
        int[] ranges = {100000000, 100000000, 10000000, 1000000, 100000, 50000, 10000, 5000, 2500,1000,500,100};
        int[] bounds = {1000000000, 1000000000, 100000000, 10000000, 1000000, 500000, 100000, 50000, 25000,10000,5000,1000};

        for (int i = 0; i < ranges.length; i++) {
            if (input >= bounds[i]) {
                return ranges[i];
            }
        }

        return 0; // 범위에 속하지 않는 경우 기본값 또는 오류 처리를 수행할 수 있습니다.
    }
}
