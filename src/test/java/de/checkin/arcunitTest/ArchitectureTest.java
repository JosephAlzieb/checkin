package de.checkin.arcunitTest;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import de.checkin.CheckinApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

  private final JavaClasses klassen =
      new ClassFileImporter()
          .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackagesOf(CheckinApplication.class);


  @Test
  @DisplayName("Das System hat eine Onion Architektur")
  void test_1() {
    ArchRule rule = onionArchitecture()
        .domainModels("de.checkin.domain..")
        .domainServices("de.checkin.domain..")
        .applicationServices("de.checkin.application..")
        .adapter("web", "de.checkin.web..")
        .adapter("persistence", "de.checkin.persistenz..");
    rule.check(klassen);
  }


}
