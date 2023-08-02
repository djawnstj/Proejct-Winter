package com.project.winter.di;

import com.project.winter.annotation.Autowired;
import com.project.winter.annotation.Component;
import com.project.winter.beans.BeanFactory;
import com.project.winter.server.WinterServerTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@WinterServerTest
public class DITest {

    public abstract static class AbstractDiTestBean {
        DiTestComponent5 component5;

        abstract void setComponent5(DiTestComponent5 component5);
    }

    @Component
    public static class DiTestBean extends AbstractDiTestBean {

        private final DiTestComponent1 component1;
        private DiTestComponent2 component2;
        private DiTestComponent3 component3;

        private DiTestBean(DiTestComponent1 component1) {
            this.component1 = component1;
        }

        @Autowired
        private DiTestBean(DiTestComponent1 component1, DiTestComponent2 component2) {
            this(component1);
            this.component2 = component2;
        }

        public DiTestBean(DiTestComponent1 component1, DiTestComponent2 component2, DiTestComponent3 component3) {
            this(component1, component2);
            this.component3 = component3;
        }

        @Autowired
        private DiTestComponent4 component4;

        @Override
        @Autowired
        void setComponent5(final DiTestComponent5 component5) {
            this.component5 = component5;
        }

    }

    @Component
    public static class SingleConstructorDiTestBean {
        private final DiTestBean diTestBean;

        SingleConstructorDiTestBean(DiTestBean diTestBean) {
            this.diTestBean = diTestBean;
        }
    }

    @Component
    public static class DiTestComponent1 {}

    @Component
    public static class DiTestComponent2 {}

    @Component
    public static class DiTestComponent3 {}

    @Component
    public static class DiTestComponent4 {}

    @Component
    public static class DiTestComponent5 {}

    @Test
    public void diTest() throws Exception {
        final DiTestBean bean = BeanFactory.getInstance().getBean(DiTestBean.class);

        assertNotNull(bean);
        assertNotNull(bean.component1);
        assertNotNull(bean.component2);
        assertNull(bean.component3);
        assertNotNull(bean.component4);
        assertNotNull(bean.component5);
    }

    @Test
    public void singleConstructorDiTest() throws Exception {
        final SingleConstructorDiTestBean bean = BeanFactory.getInstance().getBean(SingleConstructorDiTestBean.class);

        assertNotNull(bean);
        assertNotNull(bean.diTestBean);
    }

}
