package com.adyen.checkout.web;

import com.adyen.checkout.ApplicationProperty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckoutController.class)
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationProperty applicationProperty;

    @Test
    void index_returnsIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void preview_returnsPreviewViewWithType() throws Exception {
        mockMvc.perform(get("/preview").param("type", "card"))
                .andExpect(status().isOk())
                .andExpect(view().name("preview"))
                .andExpect(model().attribute("type", "card"));
    }

    @Test
    void preview_withDropinType() throws Exception {
        mockMvc.perform(get("/preview").param("type", "dropin"))
                .andExpect(status().isOk())
                .andExpect(view().name("preview"))
                .andExpect(model().attribute("type", "dropin"));
    }

    @Test
    void preview_missingTypeParam_returns400() throws Exception {
        mockMvc.perform(get("/preview"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void checkoutDropin_returnsDropinViewWithClientKey() throws Exception {
        when(applicationProperty.getClientKey()).thenReturn("test-client-key");

        mockMvc.perform(get("/checkout/dropin"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/dropin"))
                .andExpect(model().attribute("clientKey", "test-client-key"));
    }

    @Test
    void checkoutCard_returnsCardViewWithClientKey() throws Exception {
        when(applicationProperty.getClientKey()).thenReturn("test-client-key");

        mockMvc.perform(get("/checkout/card"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/card"))
                .andExpect(model().attribute("clientKey", "test-client-key"));
    }

    @Test
    void checkoutGooglepay_returnsGooglepayViewWithClientKey() throws Exception {
        when(applicationProperty.getClientKey()).thenReturn("test-client-key");

        mockMvc.perform(get("/checkout/googlepay"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/googlepay"))
                .andExpect(model().attribute("clientKey", "test-client-key"));
    }

    @Test
    void checkoutIdeal_returnsIdealViewWithClientKey() throws Exception {
        when(applicationProperty.getClientKey()).thenReturn("test-client-key");

        mockMvc.perform(get("/checkout/ideal"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/ideal"))
                .andExpect(model().attribute("clientKey", "test-client-key"));
    }

    @Test
    void checkoutSepa_returnsSepaViewWithClientKey() throws Exception {
        when(applicationProperty.getClientKey()).thenReturn("test-client-key");

        mockMvc.perform(get("/checkout/sepa"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/sepa"))
                .andExpect(model().attribute("clientKey", "test-client-key"));
    }

    @Test
    void checkoutKlarna_returnsKlarnaViewWithClientKey() throws Exception {
        when(applicationProperty.getClientKey()).thenReturn("test-client-key");

        mockMvc.perform(get("/checkout/klarna"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/klarna"))
                .andExpect(model().attribute("clientKey", "test-client-key"));
    }

    @Test
    void result_successType() throws Exception {
        mockMvc.perform(get("/result/success"))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attribute("type", "success"));
    }

    @Test
    void result_failedType() throws Exception {
        mockMvc.perform(get("/result/failed"))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attribute("type", "failed"));
    }

    @Test
    void result_errorType() throws Exception {
        mockMvc.perform(get("/result/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attribute("type", "error"));
    }

    @Test
    void result_pendingType() throws Exception {
        mockMvc.perform(get("/result/pending"))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attribute("type", "pending"));
    }
}
