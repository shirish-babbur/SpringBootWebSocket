package org.webonise.springwebsockerstockticker.Controller.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.stereotype.Controller;
import org.webonise.springwebsockerstockticker.Controller.Model.Stock;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class TickerContoller {
    @Autowired
    private SimpMessagingTemplate template;
    private TaskScheduler scheduler = new ConcurrentTaskScheduler();
    private List<Stock> stockList = new ArrayList<Stock>();
    private Random random = new Random(System.currentTimeMillis());

    public void updatePriceandBroadcast() {
        for (Stock stock : stockList) {
            int randomNumber = random.nextInt(10);
            if (random.nextInt(2) == 1) randomNumber = -randomNumber;
            stock.setPrice(stock.getPrice() + randomNumber);
            if (stock.getPrice() > Integer.MAX_VALUE) {
                System.out.println("we have reset stock price");
                stock.setPrice(randomNumber);
            }
            template.convertAndSend("/topic/prices", stockList);
        }

    }

    @PostConstruct
    public void boradcastTimePeriodically() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                updatePriceandBroadcast();
            }
        }, 1000);
    }

    @MessageMapping("/addStock")
    public void addStock(Stock stock) {
        stockList.add(stock);
        updatePriceandBroadcast();
    }

    @MessageMapping("/removeAll")
    public void removeAll() {
        stockList.clear();
        updatePriceandBroadcast();
    }
}
