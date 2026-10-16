package com.springai.multiagent.services;

import com.springai.multiagent.agents.BudgetAgent;
import com.springai.multiagent.agents.ItineraryAgent;
import com.springai.multiagent.agents.ResearchAgent;
import com.springai.multiagent.agents.ReviewAgent;
import com.springai.multiagent.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripPlanningService {

    private static final Logger log = LoggerFactory.getLogger(TripPlanningService.class);

    private final ResearchAgent researchAgent;
    private final BudgetAgent budgetAgent;
    private final ItineraryAgent itineraryAgent;
    private final ReviewAgent reviewAgent;

    public TripPlanningService(ResearchAgent researchAgent, BudgetAgent budgetAgent, ItineraryAgent itineraryAgent,
                               ReviewAgent reviewAgent) {
        this.researchAgent = researchAgent;
        this.budgetAgent = budgetAgent;
        this.itineraryAgent = itineraryAgent;
        this.reviewAgent = reviewAgent;
    }

    public TripPlanningResponse plan(TripRequest request) {

        log.info("Trip planning workflow started: destination={}, days={}", request.destination(), request.numberOfDays());

        long workflowStartTime = System.currentTimeMillis();

        TripState state = TripState.start(request);

        ResearchResult research = researchAgent.execute(request);
        state = state.withResearch(research);

        BudgetResult budget = budgetAgent.execute(state);
        state = state.withBudget(budget);

        if (!budget.withinBudget()) {
            log.warn("Trip exceeds budget: requestedBudget={}, " + "estimatedCost={}", request.budget(), budget.totalCost());

            ReviewResult review = new ReviewResult(false, "The proposed trip exceeds the available budget.",
                    List.of("Estimated trip cost is higher than " + "the requested budget."),
                    List.of("Reduce accommodation cost", "Remove lower-priority activities", "Use public transport"));

            return new TripPlanningResponse(request, research, budget, null, review, "BUDGET_EXCEEDED");
        }

        ItineraryResult itinerary = itineraryAgent.execute(state);
        state = state.withItinerary(itinerary);

        ReviewResult review = reviewAgent.execute(state);

        long totalExecutionTime = System.currentTimeMillis() - workflowStartTime;

        log.info("Trip planning workflow completed: " + "destination={}, executionTimeMs={}, valid={}",
                request.destination(), totalExecutionTime, review.valid());

        return new TripPlanningResponse(request, research, budget, itinerary, review, review.valid() ?
                "COMPLETED" : "REVIEW_FAILED");
    }
}
