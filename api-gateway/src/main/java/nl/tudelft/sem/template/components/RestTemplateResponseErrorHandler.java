package nl.tudelft.sem.template.components;


import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class RestTemplateResponseErrorHandler
        implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse)
            throws IOException {

        return (
                httpResponse.getStatusCode().series() == CLIENT_ERROR
                        || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse)
            throws IOException {

        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, httpResponse.getStatusText());

        } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, httpResponse.getStatusText());
            } else if (httpResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, httpResponse.getStatusText());
            } else if (httpResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, httpResponse.getStatusText());
            } else if (httpResponse.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, httpResponse.getStatusText());
            } else if (httpResponse.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, httpResponse.getStatusText());
            } else if (httpResponse.getStatusCode() == HttpStatus.PRECONDITION_FAILED) {
                throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, httpResponse.getStatusText());
            } else if (httpResponse.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, httpResponse.getStatusText());
            } else if (httpResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, httpResponse.getStatusText());
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, httpResponse.getStatusText());

        }
    }
}
