/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.service.resource.type;

import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.agreement.AgreementFactory;
import io.dataspaceconnector.repository.AgreementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {AgreementService.class})
public class AgreementServiceTest {

    @MockBean
    private AgreementRepository agreementRepository;

    @MockBean
    private AgreementFactory agreementFactory;

    @Autowired
    private AgreementService agreementService;

    @Test
    public void confirmAgreement_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> agreementService.confirmAgreement(null));
    }

    @Test
    public void confirmAgreement_agreementEqual_persistConfirmationAndReturnTrue() {
        /* ARRANGE */
        final var agreement = getAgreement();

        when(agreementRepository.findById(agreement.getId())).thenReturn(Optional.of(agreement));
        doNothing().when(agreementRepository).confirmAgreement(agreement.getId());

        /* ACT */
        final var result = agreementService.confirmAgreement(agreement);

        /* ASSERT */
        assertTrue(result);
        verify(agreementRepository, times(1)).confirmAgreement(agreement.getId());
    }

    @Test
    public void confirmAgreement_agreementNotEqual_returnFalse() {
        /* ARRANGE */
        final var agreement = getAgreement();

        when(agreementRepository.findById(agreement.getId())).thenReturn(Optional.of(new Agreement()));

        /* ACT */
        final var result = agreementService.confirmAgreement(agreement);

        /* ASSERT */
        assertFalse(result);
        verify(agreementRepository, never()).confirmAgreement(agreement.getId());
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private Agreement getAgreement() {
        final var agreement = new Agreement();
        ReflectionTestUtils.setField(agreement, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(agreement, "value", "someValue");
        return agreement;
    }
}
