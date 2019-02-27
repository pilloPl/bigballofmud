package io.pillopl.acl;

import io.pillopl.acl.toggles.NewModelToggles;
import io.pillopl.bigballofmud.BigBallOfMud;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.togglz.core.manager.FeatureManager;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BigBallOfMud.class, AclConfiguration.class})
public class SanityFeatureToggleTest {

    @Autowired
    FeatureManager manager;

    @Test
    public void makeSureNewModelIsDisabledOnProd() {
        assertThat(manager.isActive(NewModelToggles.RECONCILE_AND_USE_NEW_MODEL)).isFalse();
    }

}