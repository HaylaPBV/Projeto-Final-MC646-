package org.isf.vacmanager.test;

import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.service.VaccineIoOperations;
import org.isf.vactype.model.VaccineType;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VaccineBrowserManagerTest {
    private VaccineBrowserManager manager;
    private MockVaccineIoOperations mockIoOperations;

    @BeforeEach
    public void setup() {
        manager = new VaccineBrowserManager();
        mockIoOperations = new MockVaccineIoOperations();
        ReflectionTestUtils.setField(manager, "ioOperations", mockIoOperations);
        mockIoOperations.resetVaccines(); // reset vaccines before each test
    }

    @Test
    public void testGetAllVaccines() throws OHServiceException {
        List<Vaccine> vaccines = manager.getVaccine();
        assertEquals(2, vaccines.size());
    }

    @ParameterizedTest
    @MethodSource("provideVaccinesForAddition")
    public void testAddVaccine(Vaccine newVaccine, int expectedSize, Class<? extends Exception> expectedException) throws OHServiceException {
        if (expectedException != null) {
            assertThrows(expectedException, () -> manager.newVaccine(newVaccine));
        } else {
            manager.newVaccine(newVaccine);
            List<Vaccine> vaccines = manager.getVaccine();
            System.out.println("Vaccines after addition: " + vaccines.stream().map(Vaccine::getDescription).collect(Collectors.toList())); 
            assertEquals(expectedSize, vaccines.size());
        }
    }

    static Stream<Arguments> provideVaccinesForAddition() {
        return Stream.of(
                Arguments.of(createVaccine("0000000010", "B", "Pregnant"), 3, null),
                Arguments.of(createVaccine("", "E", "Child"), 2, OHDataValidationException.class), // Code is empty
                Arguments.of(createVaccine("12345678901", "F", "Child"), 2, OHDataValidationException.class), // Code is too long
                Arguments.of(createVaccine("2", "", "Child"), 2, OHDataValidationException.class), // Description is empty
                Arguments.of(createVaccine("1", "D", "Child"), 2, OHDataIntegrityViolationException.class) // Code is already in use
        );
    }

    @ParameterizedTest
    @MethodSource("provideVaccinesForRemoval")
    public void testRemoveVaccine(Vaccine vaccineToRemove, int expectedSize) throws OHServiceException {
    	System.out.println("Removing vaccine: " + vaccineToRemove.getDescription());
        manager.deleteVaccine(vaccineToRemove);
        List<Vaccine> vaccines = manager.getVaccine();
        System.out.println("Vaccines after removal: " + vaccines.stream().map(Vaccine::getDescription).collect(Collectors.toList()));
        assertEquals(expectedSize, vaccines.size());
    }

    static Stream<Arguments> provideVaccinesForRemoval() {
        return Stream.of(
                Arguments.of(createVaccine("1", "A", "Child"), 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideVaccinesForEditing")
    public void testEditVaccine(Vaccine vaccineToEdit, Vaccine updatedVaccine, int expectedSize) throws OHServiceException {
    	System.out.println("Editing vaccine: " + vaccineToEdit.getDescription());
        manager.updateVaccine(updatedVaccine);
        List<Vaccine> vaccines = manager.getVaccine();
        System.out.println("Vaccines after update: " + vaccines.stream().map(Vaccine::getDescription).collect(Collectors.toList())); // Debug statement
        assertEquals(expectedSize, vaccines.size());
        Vaccine editedVaccine = manager.getVaccine().stream().filter(v -> v.getCode().equals(updatedVaccine.getCode())).findFirst().orElse(null);
        assertEquals(updatedVaccine.getDescription(), editedVaccine.getDescription());
        assertEquals(updatedVaccine.getVaccineType().getCode(), editedVaccine.getVaccineType().getCode());
    }

    static Stream<Arguments> provideVaccinesForEditing() {
        return Stream.of(
                Arguments.of(createVaccine("1", "A", "Child"), createVaccine("1", "G", "Child"), 2),
                Arguments.of(createVaccine("3", "C", "Not Pregnant"), createVaccine("3", "C", "Pregnant"), 2)
        );
    }
    
    @ParameterizedTest
    @MethodSource("provideVaccinesForFinding")
    public void testFindVaccine(String code, Vaccine expectedVaccine) throws OHServiceException {
        System.out.println("Finding vaccine with code " + code + "...");
        Vaccine foundVaccine = manager.findVaccine(code);
        if (foundVaccine != null) {
            System.out.println("Vaccine with code " + code + " found: " + foundVaccine.getDescription());
        } else {
            System.out.println("Vaccine with code " + code + " not found");
        }
        if (expectedVaccine == null) {
            assertNull(foundVaccine);
        } else {
            assertEquals(expectedVaccine.getCode(), foundVaccine.getCode());
            assertEquals(expectedVaccine.getDescription(), foundVaccine.getDescription());
            assertEquals(expectedVaccine.getVaccineType().getCode(), foundVaccine.getVaccineType().getCode());
        }
    }

    static Stream<Arguments> provideVaccinesForFinding() {
        return Stream.of(
                Arguments.of("1", createVaccine("1", "A", "Child")),
                Arguments.of("3", createVaccine("3", "C", "Not Pregnant")),
                Arguments.of("0000000010", null) // Non-existent vaccine
        );
    }

    private static Vaccine createVaccine(String code, String description, String vaccineTypeCode) {
        VaccineType vaccineType = new VaccineType();
        setField(vaccineType, "code", vaccineTypeCode);
        return new Vaccine(code, description, vaccineType);
    }

    private static void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class MockVaccineIoOperations extends VaccineIoOperations {
        private List<Vaccine> vaccines;

        public MockVaccineIoOperations() {
            resetVaccines();
        }

        public void resetVaccines() {
            vaccines = new ArrayList<>();
            // Initialize vaccines
            vaccines.add(createVaccine("1", "A", "Child"));
            vaccines.add(createVaccine("3", "C", "Not Pregnant"));
        }

        @Override
        public List<Vaccine> getVaccine(String vaccineTypeCode) {
            if (vaccineTypeCode == null) {
                return new ArrayList<>(vaccines);
            }
            return vaccines.stream()
                    .filter(v -> v.getVaccineType().getCode().equals(vaccineTypeCode))
                    .collect(Collectors.toList());
        }

        @Override
        public Vaccine newVaccine(Vaccine vaccine) throws OHDataIntegrityViolationException, OHDataValidationException {
            if (isCodePresent(vaccine.getCode())) {
                throw new OHDataIntegrityViolationException(
                        new org.isf.utils.exception.model.OHExceptionMessage(
                                "Error",
                                "Code already in use",
                                org.isf.utils.exception.model.OHSeverityLevel.ERROR));
            }
            List<OHExceptionMessage> errors = new ArrayList<>();
            if (vaccine.getCode().isEmpty()) {
                errors.add(new OHExceptionMessage("Error", "Please insert a code", org.isf.utils.exception.model.OHSeverityLevel.ERROR));
            }
            if (vaccine.getDescription().isEmpty()) {
                errors.add(new OHExceptionMessage("Error", "Please insert a valid description", org.isf.utils.exception.model.OHSeverityLevel.ERROR));
            }
            if (vaccine.getCode().length() > 10) {
                errors.add(new OHExceptionMessage("Error", "The code is too long (max 10 characters)", org.isf.utils.exception.model.OHSeverityLevel.ERROR));
            }
            if (!errors.isEmpty()) {
                throw new OHDataValidationException(errors);
            }
            System.out.println("Adding vaccine: " + vaccine.getDescription());
            vaccines.add(vaccine);
            return vaccine;
        }

        @Override
        public Vaccine updateVaccine(Vaccine vaccine) {
            for (int i = 0; i < vaccines.size(); i++) {
                if (vaccines.get(i).getCode().equals(vaccine.getCode())) {
                    vaccines.set(i, vaccine);
                    return vaccine;
                }
            }
            throw new RuntimeException("Vaccine not found");
        }

        @Override
        public boolean deleteVaccine(Vaccine vaccine) {
            return vaccines.removeIf(v -> v.getCode().equals(vaccine.getCode()));
        }

        @Override
        public boolean isCodePresent(String code) {
            return vaccines.stream().anyMatch(v -> v.getCode().equals(code));
        }
        
        @Override
        public Vaccine findVaccine(String code) {
            return vaccines.stream()
                    .filter(v -> v.getCode().equals(code))
                    .findFirst()
                    .orElse(null);
        }
    }
}
