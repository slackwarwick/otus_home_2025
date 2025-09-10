package ru.otus.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@SuppressWarnings({"java:S1989"})
public class ClientsApiServlet extends HttpServlet {

    private static final int ID_PATH_PARAM_POSITION = 1;

    private final transient DBServiceClient serviceClient;
    private final transient Gson gson;

    public ClientsApiServlet(DBServiceClient serviceClient, Gson gson) {
        this.serviceClient = serviceClient;
        this.gson = gson;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Client user = serviceClient.getClient(extractIdFromRequest(request)).orElse(null);

        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(user));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Client client = extractClientFromRequest(request);
        System.out.println(client);
        Client savedClient = serviceClient.saveClient(client);
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.print(gson.toJson(savedClient));
    }

    private long extractIdFromRequest(HttpServletRequest request) {
        String[] path = request.getPathInfo().split("/");
        String id = (path.length > 1) ? path[ID_PATH_PARAM_POSITION] : String.valueOf(-1);
        return Long.parseLong(id);
    }

    private Client extractClientFromRequest(HttpServletRequest request) {
        try {
            Scanner scanner = new Scanner(request.getInputStream(), StandardCharsets.UTF_8);
            String jsonData = scanner.useDelimiter("\\A").next();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonData, Client.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
