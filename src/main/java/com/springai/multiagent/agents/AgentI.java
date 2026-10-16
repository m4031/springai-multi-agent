package com.springai.multiagent.agents;

public interface AgentI<I, O> {

    String name();

    O execute(I input);
}
