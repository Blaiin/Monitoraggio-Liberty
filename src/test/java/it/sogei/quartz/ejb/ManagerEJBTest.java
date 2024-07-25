package it.sogei.quartz.ejb;

import it.sogei.data_access.repositories.ConfigRepository;
import it.sogei.data_access.service.ConfigService;
import it.sogei.structure.data.Config;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@EnableAutoWeld
@AddBeanClasses({ManagerEJB.class, SchedulerEJB.class, ConfigService.class, ConfigRepository.class})
@ExtendWith(MockitoExtension.class)
public class ManagerEJBTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ConfigRepository configRepository;

    @Inject
    private ManagerEJB managerEJB;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<Config> criteriaQuery = mock(CriteriaQuery.class);
        Root<Config> root = mock(Root.class);
        TypedQuery<Config> typedQuery = mock(TypedQuery.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Config.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Config.class)).thenReturn(root);
        when(criteriaQuery.select(root)).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(new Config(), new Config()));
    }

    @Test
    public void testPostConstruct() {
        if (managerEJB != null) {
            System.out.println("ManagerEJB is injected.");
        } else {
            System.out.println("ManagerEJB not injected.");
        }
        assertNotNull(managerEJB, "ManagerEJB not injected.");

        List<Config> configs = configRepository.findAll();

        if (configs != null) {
            System.out.println("Configs are initialized.");
        } else {
            System.out.println("Configs not initialized.");
        }
        assertNotNull(configs, "Configs not initialized.");

        if (!configs.isEmpty()) {
            System.out.println("Configs list is not empty.");
        } else {
            System.out.println("Configs list is empty.");
        }
        assert(!configs.isEmpty());
    }
}
