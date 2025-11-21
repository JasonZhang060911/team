package use_case.weather;

import entity.DailyForecast;

/**
 * AdviceService: generate human-friendly advice from a daily forecast.
 * Implementation can be rule-based or LLM-based.
 */
public interface AdviceService {
    String makeAdvice(DailyForecast forecast);
}
