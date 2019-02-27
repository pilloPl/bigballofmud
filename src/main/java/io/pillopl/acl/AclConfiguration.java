package io.pillopl.acl;

import io.pillopl.acl.reconciliation.Reconciliation;
import io.pillopl.acl.reconciliation.Reconciliation.Reaction;
import io.pillopl.newmodel.lending.application.LendingFacade;
import io.pillopl.newmodel.lending.infrastructure.NewModelConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(NewModelConfig.class)
public
class AclConfiguration {

    @Bean
    LendingACL lendingACL(LendingFacade lendingFacade) {
        return new LendingACL(new Reconciliation<>(Reaction.justLog()), lendingFacade);
    }



}
