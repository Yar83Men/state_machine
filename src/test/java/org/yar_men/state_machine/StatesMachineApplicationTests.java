package org.yar_men.state_machine;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.test.context.junit4.SpringRunner;
import org.yar_men.state_machine.config.StateMachineConfig;
import org.yar_men.state_machine.domain.enums.Events;
import org.yar_men.state_machine.domain.enums.States;
import org.yar_men.state_machine.domain.model.InMemoryStateMachinePersist;

import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
class StatesMachineApplicationTests {
	@Autowired
	private StateMachine<States, Events> stateMachine;

	@Test
	void testStateMachine() {
		Assertions.assertNotNull(stateMachine);
		Assertions.assertEquals(stateMachine.getState().getId(), States.BACKLOG);
	}

	@Test
	void testStateMachineEvent() {
		stateMachine.sendEvent(Events.START_FEATURE);
		stateMachine.sendEvent(Events.DEPLOY);
		stateMachine.sendEvent(Events.FINISH_FEATURE);
		stateMachine.sendEvent(Events.QA_TEAM_APPROVE);

		Assertions.assertEquals(stateMachine.getState().getId(), States.DONE);
	}

	@Test
	void testStateMachineEventEventNotAccepted() {
		stateMachine.sendEvent(Events.START_FEATURE);
		stateMachine.sendEvent(Events.QA_TEAM_APPROVE);

		Assertions.assertEquals(stateMachine.getState().getId(), States.IN_PROGRESS);
	}

	@Test
	void testStateMachineEventEventRockStar() {
		stateMachine.sendEvent(Events.ROCK_STAR);
		Assertions.assertEquals(stateMachine.getState().getId(), States.BACKLOG);
	}

	@Test
	void testStateMachineEventEventRockStarDeployBefore() {
		stateMachine.sendEvent(Events.DEPLOY);
		stateMachine.sendEvent(Events.ROCK_STAR);

		Assertions.assertEquals(stateMachine.getState().getId(), States.TESTING);
	}
}
