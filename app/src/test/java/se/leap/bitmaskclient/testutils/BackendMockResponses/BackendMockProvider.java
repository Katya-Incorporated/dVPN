/**
 * Copyright (c) 2018 LEAP Encryption Access Project and contributers
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package se.leap.bitmaskclient.testutils.BackendMockResponses;

import java.io.IOException;

import se.leap.bitmaskclient.providersetup.ProviderApiConnector;

/**
 * Created by cyberta on 10.01.18.
 */

public class BackendMockProvider {
    /**
     * This enum can be useful to provide different responses from a mocked ProviderApiConnector
     * in order to test different error scenarios
     */
    public enum TestBackendErrorCase {
        NO_ERROR,
        ERROR_CASE_UPDATED_CERTIFICATE,
        ERROR_CASE_MICONFIGURED_PROVIDER,
        ERROR_CASE_FETCH_EIP_SERVICE_CERTIFICATE_INVALID,
        ERROR_GEOIP_SERVICE_IS_DOWN,
        ERROR_GEOIP_SERVICE_IS_DOWN_TOR_FALLBACK,
        ERROR_NO_RESPONSE_BODY,         // => NullPointerException
        ERROR_DNS_RESOLUTION_ERROR,     // => UnkownHostException
        ERROR_SOCKET_TIMEOUT,           // => SocketTimeoutException
        ERROR_WRONG_PROTOCOL,           // => MalformedURLException
        ERROR_CERTIFICATE_INVALID,      // => SSLHandshakeException
        ERROR_WRONG_PORT,               // => ConnectException
        ERROR_PAYLOAD_MISSING,          // => IllegalArgumentException
        ERROR_TLS_1_2_NOT_SUPPORTED,    // => UnknownServiceException
        ERROR_UNKNOWN_IO_EXCEPTION,     // => IOException
        ERROR_INVALID_SESSION_TOKEN,
        ERROR_NO_CONNECTION,
        NO_ERROR_API_V4,
        ERROR_DNS_RESUOLUTION_TOR_FALLBACK
    }


    public static ProviderApiConnector.ProviderApiConnectorInterface provideBackendResponsesFor(TestBackendErrorCase errorCase) throws IOException {
        switch (errorCase) {
            case NO_ERROR -> {
                return new NoErrorBackendResponse();
            }
            case NO_ERROR_API_V4 -> {
                return new NoErrorBackendResponseAPIv4();
            }
            case ERROR_CASE_UPDATED_CERTIFICATE -> {
                return new UpdatedCertificateBackendResponse();
            }
            case ERROR_CASE_MICONFIGURED_PROVIDER -> {
                return new MisconfiguredProviderBackendResponse();
            }
            case ERROR_CASE_FETCH_EIP_SERVICE_CERTIFICATE_INVALID -> {
                return new EipServiceJsonInvalidCertificateBackendResponse();
            }
            case ERROR_GEOIP_SERVICE_IS_DOWN -> {
                return new GeoIpServiceIsDownBackendResponse();
            }
            case ERROR_GEOIP_SERVICE_IS_DOWN_TOR_FALLBACK -> {
                return new GeoIpServiceNotReachableTorFallbackBackendResponse();
            }
            case ERROR_DNS_RESUOLUTION_TOR_FALLBACK -> {
                return new TorFallbackBackendResponse();
            }

            default -> {
                return new NoErrorBackendResponse();
            }
        }
    }
}
