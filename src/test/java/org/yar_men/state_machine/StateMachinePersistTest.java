package org.yar_men.state_machine;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.test.context.junit4.SpringRunner;
import org.yar_men.state_machine.domain.enums.Events;
import org.yar_men.state_machine.domain.enums.States;
import org.yar_men.state_machine.domain.model.InMemoryStateMachinePersist;

import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StateMachinePersistTest {

    @Autowired
    private InMemoryStateMachinePersist stateMachinePersist;

    @Autowired
    private StateMachine<States, Events> stateMachine;


    @Test
    void testStateMachinePersister() throws Exception {
        StateMachinePersister<States, Events, UUID> persister = new DefaultStateMachinePersister<>(stateMachinePersist);

        stateMachine.sendEvent(Events.START_FEATURE);
        stateMachine.sendEvent(Events.DEPLOY);
        persister.persist(stateMachine, stateMachine.getUuid());
    }
}
