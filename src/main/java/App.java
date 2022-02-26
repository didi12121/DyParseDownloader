import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        ChromeOptions chromeOptions = getChromeOptions();
        //主页链接
        String url = "";
        WebDriver webDriver = new ChromeDriver(chromeOptions);
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        webDriver.get(url);
        //判断是否加载完成列表
        Boolean aa = false;
        while (!aa) {
                    aa = webDriver.findElement(new By.ByCssSelector("#root > div > div.T_foQflM > div > div > div.ckqOrial > div.mwbaK9mv > div:nth-child(2) > div")).getText().equals("暂时没有更多了");
                webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.SPACE);
        }
        getlist(webDriver);
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("log-level=3","-–disable-gpu");
        return chromeOptions;
    }

    /**
     * 获得视频列表
     * @param webDriver
     */
    private static void getlist(WebDriver webDriver) {
        String pageSource = webDriver.getPageSource();
        String title = webDriver.getTitle();
        Document parse = Jsoup.parse(pageSource);
        List<String> collect = parse.getAllElements().stream().filter(x -> (x.tagName().equals("li") && x.className().equals("ECMy_Zdt"))).map(x -> x.getElementsByTag("a").attr("href")).collect(Collectors.toList());
        collect.forEach(x->{
            String u = "https:" + x;
            System.out.println(u);
            getVideo(u,title);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        webDriver.close();
    }

    /**
     * 下载视频
     * @param url
     * @param floderName
     */
    private static void getVideo(String url,String floderName) {
        ChromeOptions chromeOptions = getChromeOptions();
        WebDriver webDriver = new ChromeDriver(chromeOptions);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        webDriver.get(url);
        String title = webDriver.getTitle();
        String pageSource = webDriver.getPageSource();
        Document parse = Jsoup.parse(pageSource);
        String attr = null;
        try {
            attr = parse.getAllElements().stream().filter(x -> x.hasAttr("src")).filter(x -> x.tagName().equals("source")).findFirst().get().attr("src");
            String src = "https:" + attr;
            Download.getvideo(src, floderName, title);
        } catch (NoSuchElementException noSuchElementException) {
            System.out.println("重试");
            new Thread(()->getVideo(url, floderName)).start();
        }catch (NoSuchSessionException exception){
            System.out.println("关闭失败了");
        }finally {
            webDriver.close();
        }
    }
}
