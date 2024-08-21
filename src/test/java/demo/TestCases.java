package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;
    /*
     * TODO: Write your tests here with testng @Test annotation. 
     * Follow `testCase01` `testCase02`... format or what is provided in instructions
     */
    Wrappers wrappers;

 
@Test
public void testCase01() throws IOException {
    wrappers = new Wrappers(driver);

    // Step 1: Go to the website and click on the Hockey Teams link
    driver.get("https://www.scrapethissite.com/pages/");
    wrappers.clickElement(By.xpath("//*[@id='pages']/section/div/div/div/div[2]/h3/a"));

    // Initialize the ArrayList to store data
    ArrayList<HashMap<String, Object>> hockeyTeamsData = new ArrayList<>();

    // Step 2-3: Iterate through 4 pages and collect data
    for (int pageIndex = 0; pageIndex < 4; pageIndex++) {
        // Explicit wait to ensure the table is fully loaded
        WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='hockey']/div/table/tbody/tr")));

        // Retrieve all rows
        List<WebElement> rows = wrappers.findElements(By.xpath("//*[@id='hockey']/div/table/tbody/tr"));

        System.out.println("Number of rows found: " + rows.size());

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            WebElement row = rows.get(rowIndex);

            try {
                // Access each cell in the row
                String teamName = row.findElement(By.xpath("./td[1]")).getText();
                String year = row.findElement(By.xpath("./td[2]")).getText();
                String winPercentage = row.findElement(By.xpath("./td[7]")).getText();
                
                // Print or process the data
                System.out.println("Team Name: " + teamName);
                System.out.println("Year: " + year);
                System.out.println("Win Percentage: " + winPercentage);
                
                double winPercentValue = Double.parseDouble(winPercentage);

                if (winPercentValue < 40.0) {
                    HashMap<String, Object> teamData = new HashMap<>();
                    teamData.put("Epoch Time of Scrape", Instant.now().getEpochSecond());
                    teamData.put("Team Name", teamName);
                    teamData.put("Year", year);
                    teamData.put("Win %", winPercentValue);

                    hockeyTeamsData.add(teamData);
                }
            } catch (NoSuchElementException e) {
                System.out.println("Element not found in row index " + rowIndex);
            }
        }

        // Click next page if available
        if (pageIndex < 3) {
            wrappers.clickNextPageButton(By.xpath("//*[@id='hockey']/div/div[5]/div[1]/ul/li[" + (pageIndex + 3) + "]/a"));
        }
    }

    // Step 4: Convert ArrayList to JSON
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(new File("hockey-team-data.json"), hockeyTeamsData);
}

@Test
public void testCase02() throws IOException {
    wrappers = new Wrappers(driver);
    WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(10));

    // Step 1: Go to the website and click on "Oscar Winning Films"
    driver.get("https://www.scrapethissite.com/pages/");
    wrappers.clickElement(By.xpath("//*[@id=\"pages\"]/section/div/div/div/div[3]/h3/a"));

    // Initialize the ArrayList to store data
    ArrayList<HashMap<String, Object>> oscarData = new ArrayList<>();

    // Loop through each year link
    for (int year = 2010; year <= 2015; year++) {
        String yearXPath = "//*[@id=\"" + year + "\"]";
        WebElement yearLink;

        try {
            // Wait until the year link is visible and clickable
            yearLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(yearXPath)));
            yearLink.click();

            // Wait for the table to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='table-body']")));

            // Step 2: Collect top 5 movies
            List<WebElement> rows = wrappers.findElements(By.xpath("//*[@id='table-body']/tr"));
            int count = 0;

            for (WebElement row : rows) {
                if (count >= 5) break;

                String title = row.findElement(By.xpath("./td[1]")).getText();
                String nomination = row.findElement(By.xpath("./td[2]")).getText();
                String awards = row.findElement(By.xpath("./td[3]")).getText();

                boolean isWinner = "Yes".equalsIgnoreCase(awards); // Assuming awards column contains "Yes" for Best Picture winner

                // Prepare the HashMap with the collected data
                HashMap<String, Object> movieData = new HashMap<>();
                movieData.put("Epoch Time of Scrape", Instant.now().getEpochSecond());
                movieData.put("Year", year);
                movieData.put("Title", title);
                movieData.put("Nomination", nomination);
                movieData.put("Awards", awards);
                movieData.put("isWinner", isWinner);

                // Add the HashMap to the ArrayList
                oscarData.add(movieData);
                count++;
            }
            
            // Go back to the main page to select the next year
            driver.navigate().back();
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"pages\"]/section/div/div/div/div[3]/h3/a"))).click();
        } catch (Exception e) {
            System.out.println("Error processing year " + year + ": " + e.getMessage());
        }
    }

    // Step 3: Convert ArrayList to JSON
    ObjectMapper mapper = new ObjectMapper();
    File outputDir = new File("output");
    if (!outputDir.exists()) {
        outputDir.mkdir(); // Create directory if it doesn't exist
    }
    File outputFile = new File(outputDir, "oscar-winner-data.json");
    mapper.writeValue(outputFile, oscarData);

    // Assert that the file exists and is not empty
    Assert.assertTrue(outputFile.exists(), "The JSON file does not exist.");
    Assert.assertTrue(outputFile.length() > 0, "The JSON file is empty.");
}

    /*
     * Do not change the provided methods unless necessary, they will help in automation and assessment
     */
    @BeforeTest
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }
    @AfterTest
    public void endTest()
    {
        driver.close();
        driver.quit();

    }
}