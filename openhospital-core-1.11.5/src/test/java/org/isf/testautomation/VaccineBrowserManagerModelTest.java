package org.isf.testautomation;

import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.ReachedVertex;
import org.graphwalker.core.condition.TimeDuration;
import org.graphwalker.core.generator.AStarPath;
import org.graphwalker.core.generator.RandomPath;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.java.test.TestBuilder;
import org.graphwalker.java.test.TestExecutor;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.vaccine.manager.VaccineBrowserManager;
import org.isf.vaccine.model.Vaccine;
import org.isf.vaccine.service.VaccineIoOperationRepository;
import org.isf.vaccine.service.VaccineIoOperations;
import org.isf.vactype.manager.VaccineTypeBrowserManager;
import org.isf.vactype.model.VaccineType;
import org.isf.vactype.service.VacTypeIoOperation;
import org.isf.vactype.service.VaccineTypeIoOperationRepository;
import org.isf.vactype.test.TestVaccineType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


import org.isf.vaccine.test.TestVaccine;
import org.isf.vaccine.test.Tests;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })

public class VaccineBrowserManagerModelTest extends ExecutionContext implements OH {
    public final static Path MODEL_PATH = Paths.get("org/myorg/testautomation/VaccineBrowserManager.json");

	private static TestVaccine testVaccine;
	private static TestVaccineType testVaccineType;
	private static int vaccineCodeCounter = 1; // Counter for generating unique vaccine codes
	@Autowired
	VaccineIoOperations vaccineIoOperation;
	@Autowired
	VaccineIoOperationRepository vaccineIoOperationRepository;
	@Autowired
	VaccineTypeIoOperationRepository vaccineTypeIoOperationRepository;
	@Autowired
	VaccineBrowserManager vaccineBrowserManager;

	@Autowired
	VacTypeIoOperation vaccineTypeIoOperation;
	@Autowired
	VaccineTypeBrowserManager vaccineTypeBrowserManager;
	
	@BeforeClass
	public static void setUpClass() {
		testVaccine = new TestVaccine();
		testVaccineType = new TestVaccineType();
	}
	
	@AfterClass
	public static void tearDownClass() {
		testVaccine = null;
		testVaccineType = null;
	}
	
    @Before
    public void setUp() {
        // Clean up database before each test
        vaccineIoOperationRepository.deleteAll();
        vaccineTypeIoOperationRepository.deleteAll();
    }

    @After
    public void tearDown() {
        // Clean up database after each test
        vaccineIoOperationRepository.deleteAll();
        vaccineTypeIoOperationRepository.deleteAll();
    }
    /*
    @Test
    public void runSmokeTest() {
        new TestBuilder()
                .addContext(new VaccineBrowserManagerModelTest().setNextElement(new Vertex().setName("v_EmptyList").build()),
                        MODEL_PATH,
                        new AStarPath(new ReachedVertex("v_EmptyList")))
                .execute();
    }
    */
    /*
    @Test
    public void runFunctionalTest() {
        new TestBuilder()
                .addContext(new VaccineBrowserManagerModelTest().setNextElement(new Vertex().setName("v_EmptyList").build()),
                        MODEL_PATH,
                        new RandomPath(new EdgeCoverage(100)))
                .execute();
    }
    */
	/*
    @Test
    public void runStabilityTest() {
        new TestBuilder()
                .addContext(new VaccineBrowserManagerModelTest().setNextElement(new Vertex().setName("v_EmptyList").build()),
                        MODEL_PATH,
                        new RandomPath(new TimeDuration(10, TimeUnit.SECONDS)))
                .execute();
    }
	 */
	@Override
	public void v_NotEmptyList() {
		try {
			System.out.println("Running: v_NotEmptyList");
			
	        if (vaccineIoOperationRepository == null) {
	            throw new NullPointerException("vaccineIoOperationRepository is not initialized.");
	        }
			
			long elements = vaccineIoOperationRepository.count();
			if (elements == 0) {
				System.out.println("List is empty.");
			} else {
				System.out.println("List is not empty.");
			}
		} catch (Exception e) {
	        System.out.println("Error in List analyse: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	@Override
	public void v_EmptyList() {
		try {
			System.out.println("Running: v_EmptyList");
			
	        if (vaccineIoOperationRepository == null) {
	            throw new NullPointerException("vaccineIoOperationRepository is not initialized.");
	        }
			
			long elements = vaccineIoOperationRepository.count();
			if (elements == 0) {
				System.out.println("List is empty.");
			} else {
				System.out.println("List is not empty.");
			}
		} catch (Exception e) {
	        System.out.println("Error in List analyse: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	@Override
	public void v_OHException() {
	    try {
	        System.out.println("Running: v_OHException");

	        // Intentionally cause an OHException to test the handling
	        // Let's assume deleting a non-existent vaccine might throw an OHException
	        VaccineType vaccineType = testVaccineType.setup(false);
	        Vaccine newVaccine = testVaccine.setup(vaccineType, false);

	        // Save the test vaccine type and vaccine to ensure they exist
	        vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
	        vaccineIoOperationRepository.saveAndFlush(newVaccine);

	        // Delete the vaccine to make it non-existent
	        vaccineIoOperation.deleteVaccine(newVaccine);

	        // Attempt to delete the already deleted vaccine, which should cause an OHException
	        vaccineIoOperation.deleteVaccine(newVaccine);
	        
	    } catch (OHException e) {
	        System.out.println("Caught OHException: " + e.getMessage());
	        e.printStackTrace();
	        // Additional handling logic can be added here
	    } catch (Exception e) {
	        System.out.println("Caught a different exception: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
    @Override
    public void e_addVaccine() {
        try {
            System.out.println("Running: e_addVaccine");
            
            // Generate a unique vaccine code (for example, using a counter)
            String uniqueCode = "VAC" + vaccineCodeCounter++;
            
            // Use a test method to setup a VaccineType (assuming setup method is within this class)
            VaccineType vaccineType = setupVaccineType(false);
            
            // Create a new vaccine with the generated code
            Vaccine newVaccine = new Vaccine();
            newVaccine.setCode(uniqueCode);
            newVaccine.setDescription("Test Vaccine " + vaccineCodeCounter);
            newVaccine.setVaccineType(vaccineType);
            
            // Save the vaccine
            vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
            vaccineIoOperationRepository.saveAndFlush(newVaccine);
            
            System.out.println("Vaccine added successfully with code: " + newVaccine.getCode());
        } catch (OHException e) {
            System.out.println("Error adding vaccine: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Helper method to setup a VaccineType (if not already defined)
    private VaccineType setupVaccineType(boolean usingSet) throws OHException {
        // Implement your setup logic here
        // For simplicity, assuming you have a setup method within this class or a utility class
        return testVaccineType.setup(usingSet);
    }

	@Override
	public void e_removeVaccine() {
	    System.out.println("Running: e_removeVaccine");
	    try {
	        // Fetch all vaccines
	        List<Vaccine> vaccines = vaccineBrowserManager.getVaccine();
	        if (vaccines.isEmpty()) {
	            System.out.println("No vaccines found.");
	            return;
	        }

	        // Pick the first vaccine for demonstration purposes
	        Vaccine foundVaccine = vaccines.get(0);

	        try {
	            boolean result = vaccineIoOperation.deleteVaccine(foundVaccine);
	            if (result) {
	                System.out.println("Vaccine removed successfully.");
	            } else {
	                System.out.println("Failed to remove vaccine.");
	            }
	        } catch (Exception e) {
	            System.out.println("Error during vaccine removal: " + e.getMessage());
	            e.printStackTrace();
	        }
	    } catch (OHServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void e_updateVaccine() {
	    System.out.println("Running: e_updateVaccine");
	    // Fetch all vaccines
		List<Vaccine> vaccines;
		try {
			vaccines = vaccineBrowserManager.getVaccine();
			if (vaccines.isEmpty()) {
			    System.out.println("No vaccines found.");
			    return;
			}

			// Pick the first vaccine for demonstration purposes
			Vaccine foundVaccine = vaccines.get(0);

			foundVaccine.setDescription("Updated Description");
			vaccineIoOperationRepository.save(foundVaccine);
			System.out.println("Vaccine updated successfully.");
		} catch (OHServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public void e_addEmpty() {
        try {
            System.out.println("Running: e_addEmpty");
			long elements = vaccineIoOperationRepository.count();
			if (elements == 0) {
	            VaccineType vaccineType = testVaccineType.setup(false);
	            Vaccine newVaccine = testVaccine.setup(vaccineType, true);
	            
	            // Log the Vaccine object to verify its content
	            System.out.println("New Vaccine: " + newVaccine);
	            
	            vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
	            vaccineIoOperationRepository.saveAndFlush(newVaccine);
	            System.out.println("Vaccine added successfully in a empty DB with code: " + newVaccine.getCode());
			} else {
				System.out.println("DB was not empty before the vaccine adition.");
			}
        } catch (OHException e) {
            System.out.println("Error adding vaccine: " + e.getMessage());
            e.printStackTrace();
        }
	}

	@Override
	public void e_removeEmpty() {
	    System.out.println("Running: e_removeEmpty");
	    // Fetch all vaccines
		List<Vaccine> vaccines;
		try {
			vaccines = vaccineBrowserManager.getVaccine();
			if (vaccines.isEmpty()) {
			    System.out.println("No vaccines found.");
			    return;
			}

			// Pick the first vaccine for demonstration purposes
			Vaccine foundVaccine = vaccines.get(0);

			try {
			    boolean result = vaccineIoOperation.deleteVaccine(foundVaccine);
			    if (result) { 	
			        long elements = vaccineIoOperationRepository.count();
					if (elements == 0) {
						System.out.println("DB is empty after the vaccine removal.");
					} else {
						System.out.println("DB is not empty after the vaccine removal.");
					}
			    } else {
			        System.out.println("Failed to remove vaccine.");
			    }
			} catch (Exception e) {
			    System.out.println("Error during vaccine removal: " + e.getMessage());
			    e.printStackTrace();
			}
		} catch (OHServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    @Override
    public void e_addUsedCode() {    
        try {
        	long elements = vaccineIoOperationRepository.count();

            System.out.println("Running: e_addUsedCode");
            
            // Generate a unique vaccine code (for example, using a counter)
            String uniqueCode = "VAC" + vaccineCodeCounter++;
            
            // Use a test method to setup a VaccineType (assuming setup method is within this class)
            VaccineType vaccineType = setupVaccineType(false);
            
            // Create a new vaccine with the generated code
            Vaccine newVaccine = new Vaccine();
            newVaccine.setCode(uniqueCode);
            newVaccine.setDescription("Test Vaccine " + vaccineCodeCounter);
            newVaccine.setVaccineType(vaccineType);
            
            List<Vaccine> vaccines = vaccineBrowserManager.getVaccine();
	        if (vaccines.isEmpty()) {
	            System.out.println("No vaccines found.");
	            return;
	        }
            for (int i = 0; i < elements; i++) {
            	Vaccine foundVaccine = vaccines.get(i);
                if (newVaccine.getCode() == foundVaccine.getCode())
                	System.out.println("Vaccine with used code");
        	}    
        } catch (OHException e) {
            System.out.println("Error adding vaccine: " + e.getMessage());
            e.printStackTrace();
        } catch (OHServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public void e_addLongCode() {
        try {
            System.out.println("Running: e_addLongCode");
            
            // Generate a unique vaccine code (for example, using a counter)
            String uniqueCode = "VAC" + vaccineCodeCounter++;
            
            // Use a test method to setup a VaccineType (assuming setup method is within this class)
            VaccineType vaccineType = setupVaccineType(false);
            
            // Create a new vaccine with the generated code
            Vaccine newVaccine = new Vaccine();
            newVaccine.setCode(uniqueCode);
            newVaccine.setDescription("Test Vaccine " + vaccineCodeCounter);
            newVaccine.setVaccineType(vaccineType);
            if (newVaccine.getCode().length()>10)
            	System.out.println("Vaccine with long code");
        } catch (OHException e) {
            System.out.println("Error adding vaccine: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void e_addEmptyCode() {
    	try {
            
            System.out.println("Running: e_addEmptyCode");
            
            // Generate a unique vaccine code (for example, using a counter)
            String uniqueCode = "VAC" + vaccineCodeCounter++;
            
            // Use a test method to setup a VaccineType (assuming setup method is within this class)
            VaccineType vaccineType = setupVaccineType(false);
            
            // Create a new vaccine with the generated code
            Vaccine newVaccine = new Vaccine();
            newVaccine.setCode(uniqueCode);
            newVaccine.setDescription("Test Vaccine " + vaccineCodeCounter);
            newVaccine.setVaccineType(vaccineType);
            if (newVaccine.getCode().isEmpty())
            	System.out.println("Vaccine with empty code");
        } catch (OHException e) {
            System.out.println("Error adding vaccine: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void e_addEmptyDescription() {
	    try {
	    	System.out.println("Running: e_addEmptyDescription");
            
            // Generate a unique vaccine code (for example, using a counter)
            String uniqueCode = "VAC" + vaccineCodeCounter++;
            
            // Use a test method to setup a VaccineType (assuming setup method is within this class)
            VaccineType vaccineType = setupVaccineType(false);
            
            // Create a new vaccine with the generated code
            Vaccine newVaccine = new Vaccine();
            newVaccine.setCode(uniqueCode);
            newVaccine.setDescription("Test Vaccine " + vaccineCodeCounter);
            newVaccine.setVaccineType(vaccineType);
	        if (newVaccine.getDescription().isEmpty()) { 	
	        	System.out.println("Added Vaccine with Empty Description.");
	        } else {
	            System.out.println("Vaccine not found.");
	        }
	    } catch (OHException e) {
	        System.out.println("Error adding vaccine: " + e.getMessage());
	        e.printStackTrace();
	    }
    	
    }
    
	@Override
	public void e_return() {
	    try {
	        System.out.println("Running: e_return");
	        System.out.println("OH Returned.");   
	    } catch (Exception e) {
	        System.out.println("Error during return: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	private String _setupTestVaccine(boolean usingSet) throws OHException {
	    VaccineType vaccineType = testVaccineType.setup(false);
	    Vaccine vaccine = testVaccine.setup(vaccineType, usingSet);
	    vaccineTypeIoOperationRepository.saveAndFlush(vaccineType);
	    vaccineIoOperationRepository.saveAndFlush(vaccine);
	    return vaccine.getCode();
	}
}