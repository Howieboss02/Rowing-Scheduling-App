package nl.tudelft.sem.template.shared.enums;

import javax.persistence.Convert;
import nl.tudelft.sem.template.shared.converters.OutcomeConverter;

/**
 * Enum class for representing the outcome of enqueuing to an event.
 */
@Convert(converter = OutcomeConverter.class)
public enum Outcome {
  ACCEPTED,
  REJECTED;
}