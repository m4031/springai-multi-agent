package com.springai.multiagent.agents;

import com.springai.multiagent.dto.BudgetResult;
import com.springai.multiagent.dto.TripState;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class BudgetAgent implements AgentI<TripState, BudgetResult> {

    private final ChatClient chatClient;

    public BudgetAgent(@Qualifier("budgetChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String name() {
        return "budget-agents";
    }

    @Override
    public BudgetResult execute(TripState state) {
        return chatClient.prompt().user("""
                User request:
                %s
                
                Research produced by the research agents:
                %s
                
                Estimate accommodation, transportation, food and
                activity costs.
                
                The total budget must not exceed %s.
                """
                .formatted(state.request(), state.research(), state.request().budget()))
                .call().entity(BudgetResult.class);
    }
}
