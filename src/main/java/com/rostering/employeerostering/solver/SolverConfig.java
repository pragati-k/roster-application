package com.rostering.employeerostering.solver;

import com.rostering.employeerostering.constraint.EmployeeRosteringConstraintProvider;
import com.rostering.employeerostering.entity.Roster;
import com.rostering.employeerostering.entity.ShiftAssignment;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.UUID;

@Configuration
public class SolverConfig {

    @Bean
    public SolverFactory<Roster> solverFactory() {
        return SolverFactory.createFromXmlResource("solverConfig.xml");
    }

    @Bean
    public SolverManager<Roster, UUID> solverManager(SolverFactory<Roster> solverFactory) {
        return SolverManager.create(solverFactory);
    }
}
