package se.leap.bitmaskclient.eip;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static se.leap.bitmaskclient.base.models.Constants.PROVIDER_PRIVATE_KEY;
import static se.leap.bitmaskclient.base.models.Constants.PROVIDER_VPN_CERTIFICATE;
import static se.leap.bitmaskclient.testutils.TestSetupHelper.getInputAsString;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import de.blinkt.openvpn.core.ConfigParser;
import se.leap.bitmaskclient.base.models.Provider;
import se.leap.bitmaskclient.base.utils.PreferenceHelper;
import se.leap.bitmaskclient.base.utils.TimezoneHelper;
import se.leap.bitmaskclient.testutils.MockSharedPreferences;

/**
 * Created by cyberta on 18.12.18.
 */
@RunWith(DataProviderRunner.class)
public class GatewaySelectorTest {
    public static final String TAG = GatewaySelectorTest.class.getSimpleName();

    /**
     * locations": {
     "name": ""Frankfurt"",
     "timezone": "+1"
     },
     ""Seattle, WA"__wa": {
     "name": ""Seattle, WA", WA",
     "timezone": "-7"
     },
     ""Moscow"": {
     "country_code": "RU",
     "hemisphere": "N",
     "name": ""Moscow"",
     "timezone": "+3"
     },
     ""Manila"": {
     "country_code": "PH",
     "hemisphere": "N",
     "name": ""Manila"",
     "timezone": "+8"
     }
     },
     */


    PreferenceHelper preferenceHelper;
    JSONObject eipDefinition;
    @Before
    public void setup() throws IOException, JSONException {
        preferenceHelper = new PreferenceHelper(new MockSharedPreferences());
        eipDefinition = new JSONObject(getInputAsString(getClass().getClassLoader().getResourceAsStream("eip-service-four-gateways.json")));
    }

    private List<Gateway> getGateways() {
        try {
            Vector<Gateway> gatewayList = new Vector<>();
            JSONArray gateways = eipDefinition.getJSONArray("gateways");
            for (int i = 0; i < gateways.length(); i++) {
                JSONObject gw = gateways.getJSONObject(i);
                JSONObject secrets = secretsConfiguration();
                Gateway aux = new Gateway(eipDefinition, secrets, gw, null);
                gatewayList.add(aux);
            }
            return gatewayList;
        } catch (JSONException | IOException | ConfigParser.ConfigParseError e) {
           return new ArrayList<>();
        }

    }

    private JSONObject secretsConfiguration() throws IOException, JSONException {
        JSONObject result = new JSONObject();
        result.put(Provider.CA_CERT, getInputAsString(getClass().getClassLoader().getResourceAsStream("riseup.net.pem")));
        result.put(PROVIDER_PRIVATE_KEY, getInputAsString(getClass().getClassLoader().getResourceAsStream("private_rsa_key.pem")));
        result.put(PROVIDER_VPN_CERTIFICATE, getInputAsString(getClass().getClassLoader().getResourceAsStream("riseup.net.vpn_cert.pem")));
        return result;
    }

    @DataProvider
    public static Object[][] dataProviderTimezones() {
        // @formatter:off
        return new Object[][] {
                //{ -12,  "Seattle, WA" }
                { -11,  "Seattle, WA" },
                { -10, "Seattle, WA" },
                { -9, "Seattle, WA" },
                { -8, "Seattle, WA" },
                { -7, "Seattle, WA" }, // <-"Seattle, WA"
                { -6, "Seattle, WA" },
                { -5, "Seattle, WA" },
                { -4, "Seattle, WA" },
               // { -3, "Seattle, WA" },
                { -2, "Frankfurt" },
                { -1, "Frankfurt" },
                { 0, "Frankfurt" },
                { 1, "Frankfurt" }, // <- "Frankfurt"
               // { 2, "Moscow" },
                { 3, "Moscow" }, // <- "Moscow"
                { 4, "Moscow" },
                { 5, "Moscow" },
                { 6, "Manila" },
                { 7, "Manila" },
                { 8, "Manila" }, // <- "Manila"
                { 9, "Manila" },
                { 10, "Manila" },
                { 11, "Manila" },
                { 12, "Manila" }
        };
        // @formatter:on
    }

    @DataProvider
    public static Object[][] dataProviderSameDistanceTimezones() {
        // @formatter:off
        return new Object[][] {
                { -12,  "Seattle, WA", "Manila" },
                { -3, "Seattle, WA", "Frankfurt" },
                { 2, "Moscow", "Frankfurt" },

        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("dataProviderTimezones")
    public void testSelect(int timezone, String expected) {
        TimezoneHelper timezoneHelper = new TimezoneHelper(() -> timezone);
        GatewaySelector gatewaySelector = new GatewaySelector(getGateways());
        assertEquals(expected, gatewaySelector.select().getName());
    }

    @Test
    @UseDataProvider("dataProviderSameDistanceTimezones")
    public void testSelectSameTimezoneDistance(int timezone, String expected1, String expected2) {
        TimezoneHelper timezoneHelper = new TimezoneHelper(() -> timezone);
        GatewaySelector gatewaySelector = new GatewaySelector(getGateways());
        assertTrue(gatewaySelector.select().getName().equals(expected1) || gatewaySelector.select().getName().equals(expected2));
    }

    @Test
    @UseDataProvider("dataProviderSameDistanceTimezones")
    public void testNClostest_SameTimezoneDistance_chooseGatewayWithSameDistance(int timezone, String expected1, String expected2) {
        TimezoneHelper timezoneHelper = new TimezoneHelper(() -> timezone);
        GatewaySelector gatewaySelector = new GatewaySelector(getGateways());
        ArrayList<String> gateways = new ArrayList<>();
        gateways.add(gatewaySelector.select(0).getName());
        gateways.add(gatewaySelector.select(1).getName());

        assertTrue(gateways.contains(expected1) && gateways.contains(expected2));

    }

    @Test
    public void testNClostest_OneTimezonePerSet_choseSecondClosestTimezone() {
        TimezoneHelper timezoneHelper = new TimezoneHelper(() -> -4);
        GatewaySelector gatewaySelector = new GatewaySelector(getGateways());

        assertTrue("Frankfurt".equals(gatewaySelector.select(1).getName()));
    }
}