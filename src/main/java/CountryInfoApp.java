import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CountryInfoApp extends Application {
    private static final String REST_COUNTRIES_URL = "https://restcountries.com/v3.1/name/";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String countryName = "France";  // Set the country name you want to display
        String htmlContent = fetchCountryData(countryName);

        WebView webView = new WebView();
        webView.getEngine().loadContent(htmlContent);

        Scene scene = new Scene(webView, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Country Information");
        primaryStage.show();
    }

    public String fetchCountryData(String countryName) {
        String url = REST_COUNTRIES_URL + countryName;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonData = response.body().string();
                return parseToHTML(jsonData);
            } else {
                return "<html><body><h2>Data could not be retrieved. Try another country.</h2></body></html>";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "<html><body><h2>Error fetching data. Please try again later.</h2></body></html>";
        }
    }

    private String parseToHTML(String jsonData) {
        StringBuilder html = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode countryNode = mapper.readTree(jsonData).get(0);  // Get the first result

            String name = countryNode.get("name").get("common").asText();
            String capital = countryNode.get("capital").get(0).asText();
            String region = countryNode.get("region").asText();
            String subregion = countryNode.get("subregion").asText();
            long population = countryNode.get("population").asLong();
            String flagUrl = countryNode.get("flags").get("png").asText();

            html.append("<html><head><title>Country Information</title></head><body>");
            html.append("<h1>").append(name).append("</h1>");
            html.append("<img src='").append(flagUrl).append("' alt='Flag' width='200'><br><br>");
            html.append("<strong>Capital:</strong> ").append(capital).append("<br>");
            html.append("<strong>Region:</strong> ").append(region).append("<br>");
            html.append("<strong>Subregion:</strong> ").append(subregion).append("<br>");
            html.append("<strong>Population:</strong> ").append(population).append("<br>");
            html.append("</body></html>");

        } catch (Exception e) {
            e.printStackTrace();
            html.append("<html><body><h2>Error parsing data.</h2></body></html>");
        }

        return html.toString();
    }
}