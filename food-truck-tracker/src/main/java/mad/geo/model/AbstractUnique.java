package mad.geo.model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * The abstract class which can generate unique id for new instance
 */
abstract class AbstractUnique {
    protected static Set<String> stringIdSet = new HashSet<>();
    protected static Set<Integer> intIdSet = new HashSet<>();

    /**
     * Generate an random id for new instance.
     *
     * @param length the specific length of ID
     * @return the new random ID
     */
    private static String getRandomStringId(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    protected static int getUniqueIntId() {
        Random random = new Random();
        int id = random.nextInt(1000000);
        while (!intIdSet.add(id)) {
            id = random.nextInt(1000000);
        }
        return id;
    }

    protected static String getUniqueStringId() {
        String id = getRandomStringId(3);
        while (!stringIdSet.add(id)) {
            id = getRandomStringId(3);
        }
        return id;
    }
}
