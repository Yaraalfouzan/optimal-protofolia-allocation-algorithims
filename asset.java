import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Asset {
    String id;
    double expectedReturn;
    double riskLevel;
    int quantity;

    public Asset(String id, double expectedReturn, double riskLevel, int quantity) {
        this.id = id;
        this.expectedReturn = expectedReturn;
        this.riskLevel = riskLevel;
        this.quantity = quantity;
    }
}

public class PortfolioOptimization {

    public static double calculatePortfolioReturn(List<Asset> assets, int[] allocation) {
        double totalReturn = 0;
        for (int i = 0; i < assets.size(); i++) {
            totalReturn += allocation[i] * assets.get(i).expectedReturn;
        }
        return totalReturn;
    }

    public static double calculatePortfolioRisk(List<Asset> assets, int[] allocation) {
        double totalRisk = 0;
        for (int i = 0; i < assets.size(); i++) {
            totalRisk += allocation[i] * assets.get(i).riskLevel;
        }
        return totalRisk;
    }

    public static void bruteForcePortfolioOptimization(List<Asset> assets, int investmentAmount, double riskTolerance) {
        int numAssets = assets.size();
        int[] bestAllocation = new int[numAssets];
        double maxReturn = 0;
        boolean minRiskExceeded = false;

        // Generate all possible asset allocations
        for (int i = 0; i <= investmentAmount; i++) {
            for (int j = 0; j <= investmentAmount - i; j++) {
                int k = investmentAmount - i - j;
                int[] allocation = {i, j, k}; // Assuming 3 assets, adjust as per the number of assets
                double totalInvestment = i + j + k;

                // Check if total investment does not exceed the investment amount
                if (totalInvestment <= investmentAmount) {
                    // Calculate portfolio return and risk
                    double portfolioReturn = calculatePortfolioReturn(assets, allocation);
                    double portfolioRisk = calculatePortfolioRisk(assets, allocation);

                    // Check if risk is within tolerance
                    if (portfolioRisk <= riskTolerance) {
                        // Update if the return is higher than the current maximum
                        if (portfolioReturn > maxReturn) {
                            maxReturn = portfolioReturn;
                            bestAllocation = allocation.clone();
                        }
                    } else {
                        minRiskExceeded = true;
                    }
                }
            }
        }

        if (maxReturn > 0) {
            System.out.println("Best Allocation:");
            for (int i = 0; i < numAssets; i++) {
                System.out.println(assets.get(i).id + ": " + bestAllocation[i]);
            }
            System.out.println("Expected Return: " + maxReturn);
            System.out.println("Total Portfolio Risk: " + calculatePortfolioRisk(assets, bestAllocation));
            if (minRiskExceeded)
                System.out.println("Minimum risk tolerance exceeded.");
        } else {
            System.out.println("No valid allocation found within the given constraints.");
        }
    }

    public static void main(String[] args) {
        String fileName = "Example.txt"; // Adjust file name as needed
        List<Asset> assets = new ArrayList<>();
        int investmentAmount = 0;
        double riskTolerance = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(":")) {
                    String[] parts = line.split(":");
                    String id = parts[0].trim();
                    double expectedReturn = Double.parseDouble(parts[1].trim());
                    double riskLevel = Double.parseDouble(parts[2].trim());
                    int quantity = Integer.parseInt(parts[3].trim());
                    assets.add(new Asset(id, expectedReturn, riskLevel, quantity));
                } else if (line.startsWith("Total investment")) {
                    investmentAmount = Integer.parseInt(line.split(" ")[3]);
                } else if (line.startsWith("Risk tolerance level")) {
                    riskTolerance = Double.parseDouble(line.split(" ")[4]);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        bruteForcePortfolioOptimization(assets, investmentAmount, riskTolerance);
    }
}

