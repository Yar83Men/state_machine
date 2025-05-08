package org.yar_men.state_machine.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;
import org.yar_men.state_machine.domain.enums.Events;
import org.yar_men.state_machine.domain.enums.States;

import java.util.Optional;

@Slf4j
@Configuration
@EnableStateMachine
public class StateMachineConfig extends StateMachineConfigurerAdapter<States, Events> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration()
                .listener(listener())
                .autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        states.withStates()
                .initial(States.BACKLOG)
//                .states(EnumSet.allOf(State.class));
                .state(States.IN_PROGRESS, goWork())
//                .state(States.TESTING, deployAction())
                .state(States.TESTING)
                .state(States.DONE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions.withExternal()
                .source(States.BACKLOG)
                .target(States.IN_PROGRESS)
                .event(Events.START_FEATURE)
                .and()
                .withExternal()
                .source(States.IN_PROGRESS)
                .target(States.TESTING)
                .event(Events.FINISH_FEATURE)
                .guard(deployGuard())
                .and()
                .withExternal()
                .source(States.TESTING)
                .target(States.DONE)
                .event(Events.QA_TEAM_APPROVE)
                .and()
                .withExternal()
                .source(States.TESTING)
                .target(States.IN_PROGRESS)
                .event(Events.QA_TEAM_REJECT)
                .and()
                .withExternal()
                .source(States.BACKLOG)
                .target(States.TESTING)
                .guard(deployGuard())
                .event(Events.ROCK_STAR)
                .and()
                .withInternal()
                .source(States.BACKLOG)
                .event(Events.DEPLOY)
                .action(deployAction())
                .and()
                .withInternal()
                .source(States.IN_PROGRESS)
                .event(Events.DEPLOY)
                .action(deployAction());
    }

    private Guard<States, Events> deployGuard() {
        return context -> (Boolean) context.getExtendedState().getVariables().get("deployed");
    }

    private StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void transition(Transition<States, Events> transition) {
               log.info("Transitioning from {} to {}",
                       Optional.ofNullable(transition.getSource())
                               .map(org.springframework.statemachine.state.State::getId)
                               .orElse(null),
                       Optional.ofNullable(transition.getTarget())
                               .map(org.springframework.statemachine.state.State::getId)
                               .orElse(null));
            }

            @Override
            public void eventNotAccepted(Message<Events> event) {
                log.error("Event not accepted {}", event);
            }
        };
    }

    private Action<States, Events> goWork() {
        return context -> log.info("Time to codding");
    }

    private Action<States, Events> deployAction() {
        return context -> {
            context.getExtendedState().getVariables().put("deployed", true);
            log.info("Deploying app to k8s");
        };
    }
}
