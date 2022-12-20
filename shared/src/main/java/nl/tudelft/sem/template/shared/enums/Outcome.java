package nl.tudelft.sem.template.shared.enums;

import nl.tudelft.sem.template.shared.converters.OutcomeConverter;

import javax.persistence.Convert;

/**
 * Enum class for representing the outcome of enqueuing to an event.
 */
@Convert(converter = OutcomeConverter.class)
public enum Outcome {
  ACCEPTED,
  REJECTED;
}