package gui;

import detection.AnomalyStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MerchantTableViewColorTest {

    @Test
    void colorFor_highAnomaly_isRed() {
        assertEquals("#d9534f", MerchantTableView.colorFor(AnomalyStatus.HIGH_ANOMALY.toString()));
    }

    @Test
    void colorFor_mediumAnomaly_isOrange() {
        assertEquals("#e07b39", MerchantTableView.colorFor(AnomalyStatus.MEDIUM_ANOMALY.toString()));
    }

    @Test
    void colorFor_lowAnomaly_isYellow() {
        assertEquals("#c9a227", MerchantTableView.colorFor(AnomalyStatus.LOW_ANOMALY.toString()));
    }

    @Test
    void colorFor_normal_isGrey() {
        assertEquals("#555555", MerchantTableView.colorFor(AnomalyStatus.NORMAL.toString()));
    }
}