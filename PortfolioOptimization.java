import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PortfolioOptimization {
    
    
    public static void main(String[] args) {
        List<Asset> assets = new ArrayList<>();
        int totalInvestment = 0;
        double riskTolerance = 0.0;

        try {
            File file = new File("Example.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" : ");
                if (parts.length == 4) {
                    String id = parts[0];
                    double expectedReturn = Double.parseDouble(parts[1]);
                    double riskLevel = Double.parseDouble(parts[2]);
                    int quantity = Integer.parseInt(parts[3]);
                    assets.add(new Asset(id, expectedReturn, riskLevel, quantity));
                } else if (parts.length == 1) {
                    if (parts[0].startsWith("Total investment is")) {
                        totalInvestment = Integer.parseInt(parts[0].split(" ")[3]);
                    } else if (parts[0].startsWith("Risk tolerance level is")) {
                        riskTolerance = Double.parseDouble(parts[0].split(" ")[4]);
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bruteForcePortfolioOptimization(assets, totalInvestment, riskTolerance);
    }

    public static void bruteForcePortfolioOptimization(List<Asset> assets, int totalInvestment, double riskTolerance) {
        int n = assets.size();
        int[] bestAllocation = null;
        double maxReturn = 0;
        boolean minRiskExceeded = false;

        for (int i = 0; i <= totalInvestment; i++) {
            for (int j = 0; j <= totalInvestment; j++) {
                for (int k = 0; k <= totalInvestment; k++) {
                    if (i + j + k <= totalInvestment) {
                        int[] allocation = {i, j, k};
                        double totalRisk = 0;
                        double totalReturn = 0;

                        for (int l = 0; l < n; l++) {
                            totalRisk += allocation[l] * assets.get(l).riskLevel;
                            totalReturn += allocation[l] * assets.get(l).expectedReturn;
                        }

                        if (totalRisk <= riskTolerance) {
                            if (totalReturn > maxReturn) {
                                maxReturn = totalReturn;
                                bestAllocation = allocation;
                            }
                        } else {
                            minRiskExceeded = true;
                        }
                    }
                }
            }
        }

        if (bestAllocation != null) {
            System.out.println("Best Allocation:");
            for (int i = 0; i < n; i++) {
                System.out.println(assets.get(i).id + ": " + bestAllocation[i] + " units");
            }
            System.out.println("Expected Return: " + maxReturn);
            System.out.println("Total Risk: " + calculatePortfolioRisk(assets, bestAllocation));
            if (minRiskExceeded) {
                System.out.println("Risk tolerance level exceeded for some allocations.");
            }
        } else {
            System.out.println("No valid allocation found within risk tolerance.");
        }
    }

    public static double calculatePortfolioRisk(List<Asset> assets, int[] allocation) {
        double totalRisk = 0;
        for (int i = 0; i < assets.size(); i++) {
            totalRisk += allocation[i] * assets.get(i).riskLevel;
        }
        return totalRisk;
    }
}
