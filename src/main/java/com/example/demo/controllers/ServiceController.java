package com.example.demo.controllers;
import java.util.LinkedList;
import java.util.Queue;
import com.example.demo.model.Order;
import com.example.demo.model.User;
import com.example.demo.services.OrderService;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/service")
public class ServiceController {

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public ServiceController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    private Queue<String> feedbackQueue = new LinkedList<>();
    @GetMapping("/driver")
    public String showDriverServicesPage(Model model) {
        model.addAttribute("bodywork", false);
        model.addAttribute("order", new Order());
        model.addAttribute("latestFeedbacks", getLatestFeedbacks());
        return "driver-service";
    }

    private String getLatestFeedbacks() {
        StringBuilder feedbacks = new StringBuilder();
        for (String feedback : feedbackQueue) {
            feedbacks.append(feedback).append("\n");
        }
        return feedbacks.toString();
    }

    @GetMapping("/pedestrian")
    public String showPedestrianServicesPage(Model model) {
        model.addAttribute("order", new Order());
        return "pedestrian-service";
    }

    @PostMapping("/process")
    public String processServiceForm(Order order, Model model,
                                     String name,
                                     boolean bodywork,
                                     String bodyworkType,
                                     boolean accessories,
                                     boolean merchandise,
                                     String feedback) {
        // Логика для обработки заказа и сохранения в базе данных
        User user = userService.saveUser(new User()); // Создаем нового пользователя
        order.setUser(user); // Устанавливаем пользователя в заказ
        orderService.saveOrder(order); // Сохраняем заказ в базе данных

        model.addAttribute("user", user); // Передаем пользователя в модель
        model.addAttribute("order", order); // Передаем заказ в модель
        // Обработка данных формы и сохранение отзыва в очередь
        String formattedFeedback = name + ": " + feedback;
        feedbackQueue.offer(formattedFeedback);
        if (feedbackQueue.size() > 3) {
            feedbackQueue.poll(); // Удаляем старые отзывы, чтобы в очереди было не более 3 последних
        }
        return "confirmation"; // Переход на страницу подтверждения

    }
}