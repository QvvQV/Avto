package ru.tutor.constants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class GameWheelRewardsConstants {

    // Эталонные значения наград (ID -> данные)
    public static final Map<Long, ExpectedReward> EXPECTED_REWARDS = new HashMap<>();

    static {
        // Заполняем эталонные значения
//        EXPECTED_REWARDS.put(2L, new ExpectedReward("5 алмазов", 12.75, "diamond", 5));
//        EXPECTED_REWARDS.put(3L, new ExpectedReward("2 алмаза", 24.5, "diamond", 2));
//        EXPECTED_REWARDS.put(4L, new ExpectedReward("300 опыта", 12.75, "experience", 300));
//        EXPECTED_REWARDS.put(5L, new ExpectedReward("100 опыта", 24.5, "experience", 100));
//        EXPECTED_REWARDS.put(6L, new ExpectedReward("Сертификат озон на 500 рублей", 0.05, "valuation", 500));
//        EXPECTED_REWARDS.put(7L, new ExpectedReward("Сертификат озон на 1000 рублей", 0.125, "valuation", 1000));
//        EXPECTED_REWARDS.put(8L, new ExpectedReward("5 алмазов", 8.15, "diamond", 5));
//        EXPECTED_REWARDS.put(9L, new ExpectedReward("30 алмазов", 8.15, "diamond", 30));
//        EXPECTED_REWARDS.put(11L, new ExpectedReward("30 алмазов", 1.55, "diamond", 30));
//        EXPECTED_REWARDS.put(12L, new ExpectedReward("7 алмазов", 1.55, "diamond", 7));
//        EXPECTED_REWARDS.put(13L, new ExpectedReward("1000 Опыта", 1.55, "experience", 1000));
//        EXPECTED_REWARDS.put(14L, new ExpectedReward("400 Опыта", 3.05, "experience", 400));
//        EXPECTED_REWARDS.put(15L, new ExpectedReward("50 алмазов", 0.45, "diamond", 50));
//        EXPECTED_REWARDS.put(16L, new ExpectedReward("100 алмазов", 0.45, "diamond", 100));
//        EXPECTED_REWARDS.put(18L, new ExpectedReward("Сертификат озон на 3000 рублей", 0.01, "valuation", 3000));
//        EXPECTED_REWARDS.put(19L, new ExpectedReward("Сертификат озон на 10000 рублей", 0.004, "valuation", 10000));
//        EXPECTED_REWARDS.put(20L, new ExpectedReward("Бесплатный годовой доступ", 0.005, "valuation", 4800));
//        EXPECTED_REWARDS.put(21L, new ExpectedReward("Air pods pro 2", 0.0002, "valuation", 31990));
//        EXPECTED_REWARDS.put(22L, new ExpectedReward("Iphone 15 Pro 256 гб", 100.0, "valuation", 139990));
//        EXPECTED_REWARDS.put(23L, new ExpectedReward("Фен Dyson Supersonic HD07", 0.0002, "valuation", 39990));

// Эталонные значения из задачи https://task.cpateam.xyz/issue/TP-4632/Redizajn-kolesa-fortuny
        EXPECTED_REWARDS.put(2L, new ExpectedReward("5 алмазов", 25.0, "diamond", 5));
        EXPECTED_REWARDS.put(4L, new ExpectedReward("300 опыта", 12.76, "experience", 300));
        EXPECTED_REWARDS.put(5L, new ExpectedReward("100 опыта", 50.0, "experience", 100));
        EXPECTED_REWARDS.put(6L, new ExpectedReward("Сертификат озон на 500 рублей", 0.05, "valuation", 500));
        EXPECTED_REWARDS.put(7L, new ExpectedReward("Сертификат озон на 1000 рублей", 0.02, "valuation", 1000));
        EXPECTED_REWARDS.put(9L, new ExpectedReward("10 алмазов", 8.15, "diamond", 10));
        EXPECTED_REWARDS.put(11L, new ExpectedReward("30 алмазов", 1.55, "diamond", 30));
        EXPECTED_REWARDS.put(13L, new ExpectedReward("1000 Опыта", 1.55, "experience", 1000));
        EXPECTED_REWARDS.put(15L, new ExpectedReward("50 алмазов", 0.45, "diamond", 50));
        EXPECTED_REWARDS.put(16L, new ExpectedReward("100 алмазов", 0.45, "diamond", 100));
        EXPECTED_REWARDS.put(18L, new ExpectedReward("Сертификат озон на 3000 рублей", 0.01, "valuation", 3000));
        EXPECTED_REWARDS.put(19L, new ExpectedReward("Сертификат озон на 10000 рублей", 0.004, "valuation", 10000));
        EXPECTED_REWARDS.put(20L, new ExpectedReward("Бесплатный годовой доступ", 0.005, "valuation", 4800));
        EXPECTED_REWARDS.put(21L, new ExpectedReward("Air pods pro 2", 0.0002, "valuation", 31990));
        EXPECTED_REWARDS.put(22L, new ExpectedReward("Iphone 15 Pro 256 гб", 0.0001, "valuation", 139990));
        EXPECTED_REWARDS.put(23L, new ExpectedReward("Фен Dyson Supersonic HD07", 0.0002, "valuation", 39990));
    }

    // Допустимая погрешность для сравнения шансов
    public static final double CHANCE_TOLERANCE = 0.00;

    // Ожидаемые типы призов (для start-game)
    public static final String TYPE_VALUATION = "valuation";
    public static final String TYPE_SUBSCRIPTION = "subscription";
    public static final String TYPE_DIAMOND = "diamond";
    public static final String TYPE_EXPERIENCE = "experience";
    public static final String TYPE_BONUS = "bonus";

    // Допустимые типы призов
    public static final String[] ALLOWED_TYPES = {
            TYPE_VALUATION,
            TYPE_SUBSCRIPTION,
            TYPE_DIAMOND,
            TYPE_EXPERIENCE,
            TYPE_BONUS
    };

    // База известных призов для start-game
    private static final Map<Long, ExpectedStartGamePrize> EXPECTED_START_GAME_PRIZE = new HashMap<>();

    static {
        EXPECTED_START_GAME_PRIZE.put(2L, new ExpectedStartGamePrize(2L,"5 алмазов", TYPE_DIAMOND, 5, 5));
        EXPECTED_START_GAME_PRIZE.put(3L, new ExpectedStartGamePrize(3L, "2 алмаза", TYPE_DIAMOND, 2, 2));
        EXPECTED_START_GAME_PRIZE.put(4L, new ExpectedStartGamePrize(4L, "300 опыта", TYPE_EXPERIENCE, 300, 300));
        EXPECTED_START_GAME_PRIZE.put(5L, new ExpectedStartGamePrize(5L, "100 опыта", TYPE_EXPERIENCE, 100, 100));
        EXPECTED_START_GAME_PRIZE.put(6L, new ExpectedStartGamePrize(6L, "Сертификат озон на 500 рублей", TYPE_VALUATION, 500, 500));
        EXPECTED_START_GAME_PRIZE.put(7L, new ExpectedStartGamePrize(7L, "Сертификат озон на 1000 рублей", TYPE_VALUATION, 1000, 1000));
        EXPECTED_START_GAME_PRIZE.put(8L, new ExpectedStartGamePrize(8L, "5 алмазов", TYPE_DIAMOND, 5, 5));
        EXPECTED_START_GAME_PRIZE.put(9L, new ExpectedStartGamePrize(9L, "30 алмазов", TYPE_DIAMOND, 30, 30));
        EXPECTED_START_GAME_PRIZE.put(11L, new ExpectedStartGamePrize(11L,"30 алмазов", TYPE_DIAMOND, 30, 30));
        EXPECTED_START_GAME_PRIZE.put(12L, new ExpectedStartGamePrize(12L, "7 алмазов", TYPE_DIAMOND, 7, 7));
        EXPECTED_START_GAME_PRIZE.put(13L, new ExpectedStartGamePrize(13L, "1000 Опыта", TYPE_EXPERIENCE, 1000, 1000));
        EXPECTED_START_GAME_PRIZE.put(14L, new ExpectedStartGamePrize(14L,"400 Опыта", TYPE_EXPERIENCE, 400, 400));
        EXPECTED_START_GAME_PRIZE.put(15L, new ExpectedStartGamePrize(15L,"50 алмазов", TYPE_DIAMOND, 50, 50));
        EXPECTED_START_GAME_PRIZE.put(16L, new ExpectedStartGamePrize(16L,"100 алмазов", TYPE_DIAMOND, 100, 100));
        EXPECTED_START_GAME_PRIZE.put(18L, new ExpectedStartGamePrize(18L,"Сертификат озон на 3000 рублей", TYPE_VALUATION, 3000, 3000));
        EXPECTED_START_GAME_PRIZE.put(19L, new ExpectedStartGamePrize(19L, "Сертификат озон на 10000 рублей", TYPE_VALUATION, 10000, 10000));
        EXPECTED_START_GAME_PRIZE.put(20L, new ExpectedStartGamePrize(20L,"Бесплатный годовой доступ", TYPE_SUBSCRIPTION, 4800, 4800));
        EXPECTED_START_GAME_PRIZE.put(21L, new ExpectedStartGamePrize(21L,"Air pods pro 2", TYPE_VALUATION, 31990, 31990));
        EXPECTED_START_GAME_PRIZE.put(22L, new ExpectedStartGamePrize(22L,"Iphone 15 Pro 256 гб", TYPE_VALUATION, 139990, 139990));
        EXPECTED_START_GAME_PRIZE.put(23L, new ExpectedStartGamePrize(23L,"Фен Dyson Supersonic HD07", TYPE_VALUATION, 39990, 39990));
    }


    // Класс для хранения эталонных данных (rewards)
    public static class ExpectedReward {
        private final String name;
        private final double chance;
        private final String rewardType;
        private final Integer rewardValue;

        public ExpectedReward(String name, double chance, String rewardType, Integer rewardValue) {
            this.name = name;
            this.chance = chance;
            this.rewardType = rewardType;
            this.rewardValue = rewardValue;
        }

        public String getName() { return name; }
        public double getChance() { return chance; }
        public String getRewardType() { return rewardType; }
        public Integer getRewardValue() { return rewardValue; }

        @Override
        public String toString() {
            return String.format("%s (%.4f%%) - %s %d", name, chance, rewardType, rewardValue);
        }
    }

    // Методы для получения данных
    public static ExpectedReward getExpectedRewardById(Long id) {
        return EXPECTED_REWARDS.get(id);
    }

    public static boolean isChanceMatches(Long id, double actualChance) {
        ExpectedReward expected = getExpectedRewardById(id);
        if (expected == null) return false;

        return Math.abs(expected.getChance() - actualChance) <= CHANCE_TOLERANCE;
    }

    // Классы для хранения эталонных данных призов (start-game)
    public static class ExpectedStartGamePrize {
        private final Long id;
        private final String name;
        private final String type;
        private final Integer minValue;
        private final Integer maxValue;

        public ExpectedStartGamePrize(Long id, String name, String type, Integer minValue, Integer maxValue) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public Integer getMinValue() {
            return minValue;
        }

        public Integer getMaxValue() {
            return maxValue;
        }

        @Override
        public String toString() {
            return String.format("%s (ID: %d) - %s [%d-%d]", id, name, type, minValue, maxValue);
        }

        // ================ МЕТОДЫ ДЛЯ REWARDS ================



        public static ExpectedReward getExpectedRewardById(Long id) {
            return EXPECTED_REWARDS.get(id);
        }

        public static boolean isChanceMatches(Long id, double actualChance) {
            ExpectedReward expected = getExpectedRewardById(id);
            if (expected == null) return false;
            return Math.abs(expected.getChance() - actualChance) <= CHANCE_TOLERANCE;
        }

        // ================ МЕТОДЫ ДЛЯ START-GAME ================

        public static ExpectedStartGamePrize getExpectedStartGamePrizeById(Long id) {
            return EXPECTED_START_GAME_PRIZE.get(id);
        }

        public static boolean isStartGamePrizeKnown(Long id) {
            return EXPECTED_START_GAME_PRIZE.containsKey(id);
        }

        public static boolean validateStartGamePrizeValue(Long id, Integer value) {
            ExpectedStartGamePrize prize = getExpectedStartGamePrizeById(id);
            if (prize == null) return false;
            return value >= prize.getMinValue() && value <= prize.getMaxValue();
        }

        public static boolean validateStartGamePrizeType(Long id, String type) {
            ExpectedStartGamePrize prize = getExpectedStartGamePrizeById(id);
            if (prize == null) return false;
            return prize.getType().equals(type);
        }

        // ================ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ================

        // Проверка, является ли тип допустимым
        public static boolean isValidType(String type) {
            return Arrays.asList(ALLOWED_TYPES).contains(type);
        }

//        // Проверка URL иконки
//        public static boolean isValidIconUrl(String url) {
//            if (url == null || url.isEmpty()) return false;
//            return url.startsWith(ICON_URL_PATTERN) &&
//                    (url.endsWith(".png") || url.endsWith(".jpg") ||
//                            url.endsWith(".jpeg") || url.endsWith(".gif") ||
//                            url.endsWith(".svg"));
//        }

        // Проверка значения в зависимости от типа
        public static boolean isValueValidForType(String type, Integer value) {
            if (value == null || value <= 0) return false;

            switch (type) {
                case TYPE_VALUATION:
                    return value >= 100; // Материальные призы от 100 рублей
                case TYPE_SUBSCRIPTION:
                    return value >= 1 && value <= 36; // Подписка от 1 до 36 месяцев
                case TYPE_DIAMOND:
                case TYPE_EXPERIENCE:
                case TYPE_BONUS:
                    return value >= 1; // Любое положительное значение
                default:
                    return false;
            }
        }

        // Генерация сообщения об ошибке для значения
        public static String getValueErrorMessage(String type, Integer value) {
            switch (type) {
                case TYPE_VALUATION:
                    return String.format("Материальные призы должны быть >= 100. Получено: %d", value);
                case TYPE_SUBSCRIPTION:
                    return String.format("Подписка должна быть от 1 до 36 месяцев. Получено: %d", value);
                case TYPE_DIAMOND:
                case TYPE_EXPERIENCE:
                case TYPE_BONUS:
                    return String.format("Значение должно быть >= 1. Получено: %d", value);
                default:
                    return String.format("Недопустимый тип: %s", type);
            }
        }
    }
}
