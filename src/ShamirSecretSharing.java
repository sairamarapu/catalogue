import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.*;
import org.json.JSONObject;

public class ShamirSecretSharing {
    public static void main(String[] args) {
        try {
            JSONObject testCase1 = loadJSONFromFile("testcase1.json");
            JSONObject testCase2 = loadJSONFromFile("testcase2.json");

            System.out.println("Test Case 1 Result: " + computeConstantTerm(testCase1));
            System.out.println("Test Case 2 Result: " + computeConstantTerm(testCase2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject loadJSONFromFile(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return new JSONObject(content);
    }

    public static BigInteger computeConstantTerm(JSONObject testCase) {
        JSONObject keys = testCase.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        if (n < k) {
            throw new IllegalArgumentException("Number of roots (n) must be greater than or equal to k.");
        }

        List<BigInteger> xPoints = new ArrayList<>();
        List<BigInteger> yPoints = new ArrayList<>();

        for (String key : testCase.keySet()) {
            if (key.equals("keys")) continue;

            JSONObject point = testCase.getJSONObject(key);
            int x = Integer.parseInt(key);
            int base = point.getInt("base");
            String encodedY = point.getString("value");
            BigInteger y = new BigInteger(encodedY, base);

            xPoints.add(BigInteger.valueOf(x));
            yPoints.add(y);
        }

        return performLagrangeInterpolation(xPoints, yPoints, k);
    }

    public static BigInteger performLagrangeInterpolation(List<BigInteger> x, List<BigInteger> y, int k) {
        BigInteger constantTerm = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger currentTerm = y.get(i);
            BigInteger denominator = BigInteger.ONE;
            BigInteger numerator = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(x.get(j).negate());
                    denominator = denominator.multiply(x.get(i).subtract(x.get(j)));
                }
            }

            currentTerm = currentTerm.multiply(numerator).divide(denominator);
            constantTerm = constantTerm.add(currentTerm);
        }

        return constantTerm;
    }
}
