package org.yar_men.state_machine.domain.model;

import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.stereotype.Service;
import org.yar_men.state_machine.domain.enums.Events;
import org.yar_men.state_machine.domain.enums.States;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class InMemoryStateMachinePersist implements StateMachinePersist<States, Events, UUID> {

    private final Map<UUID, StateMachineContext<States,Events>> storage = new HashMap<>();

    @Override
    public void write(StateMachineContext<States, Events> stateMachineContext, UUID uuid) throws Exception {
        storage.put(uuid, stateMachineContext);
    }

    @Override
    public StateMachineContext<States, Events> read(UUID uuid) throws Exception {
        return storage.get(uuid);
    }
}
