package dev.o8o1o5.starforceDeBleu.data;

public enum StarforceLevel {
    // 별 개수 0부터 순서대로 정의합니다.
    // 각 상수의 인자 순서: 성공, 실패, 파괴 (모두 합쳐 100이 되어야 함)
    LEVEL_0(95, 5, 0),
    LEVEL_1(90, 10, 0),
    LEVEL_2(85, 15, 0),
    LEVEL_3(85, 15, 0),
    LEVEL_4(80, 20, 0),
    LEVEL_5(75, 25, 0),
    LEVEL_6(70, 30, 0),
    LEVEL_7(65, 35, 0),
    LEVEL_8(60, 40, 0),
    LEVEL_9(55, 45, 0),
    LEVEL_10(50, 50, 0),
    LEVEL_11(45, 55, 0),
    LEVEL_12(40, 58, 2),
    LEVEL_13(35, 63, 2),
    LEVEL_14(30, 68, 2),
    LEVEL_15(30, 66, 4),
    LEVEL_16(30, 63, 7),
    LEVEL_17(15, 76, 9),
    LEVEL_18(15, 74, 11),
    LEVEL_19(15, 72, 13),
    LEVEL_20(15, 70, 15),
    LEVEL_21(15, 69, 16),
    LEVEL_22(15, 68, 17),
    LEVEL_23(10, 72, 18),
    LEVEL_24(10, 71, 19),
    LEVEL_25(10, 70, 20)
    ;

    // private final Material displayMaterial; // 이 필드를 제거합니다.
    private final int successRate;
    private final int failRate;
    private final int destroyRate;

    // 생성자에서 displayMaterial 인자를 제거합니다.
    StarforceLevel(int successRate, int failRate, int destroyRate) {
        this.successRate = successRate;
        this.failRate = failRate;
        this.destroyRate = destroyRate;
    }

    // getDisplayMaterial() 메서드도 제거합니다.
    // public Material getDisplayMaterial() {
    //     return displayMaterial;
    // }

    public int getSuccessRate() {
        return successRate;
    }

    public int getFailRate() {
        return failRate;
    }

    public int getDestroyRate() {
        return destroyRate;
    }

    /**
     * 주어진 별 개수에 해당하는 StarforceLevel enum을 반환합니다.
     * 만약 starCount가 정의된 레벨 범위를 벗어나면, 가장 가까운 유효한 레벨을 반환합니다.
     * (예: 음수이면 0성, 정의된 최대 레벨을 초과하면 최대 레벨)
     *
     * @param starCount 현재 아이템의 별 개수
     * @return 해당 별 개수에 대한 StarforceLevel enum
     */
    public static StarforceLevel getLevel(int starCount) {
        StarforceLevel[] levels = values(); // 모든 StarforceLevel enum 상수 배열

        // 별 개수가 0 미만인 경우 (예외 처리)
        if (starCount < 0) {
            return levels[0]; // 0성 (LEVEL_0)으로 처리
        }
        // 별 개수가 정의된 최대 레벨을 초과하는 경우
        if (starCount >= levels.length) {
            return levels[levels.length - 1]; // 정의된 가장 높은 레벨로 처리
        }
        // 유효한 범위 내의 별 개수인 경우
        return levels[starCount];
    }
}
