package ru.otus.exchange.bench.xsd;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import ru.otus.exchange.xsdschema.EnvelopeType;

public class JAXBTest {

    @State(Scope.Benchmark)
    public static class ExecutionPlan {
        @Param({"blob", "ref"})
        public String type;

        private final Map<String, byte[]> samples = new HashMap<>();

        private JAXBContext context;

        @Setup(Level.Trial)
        public void setUpTrial() throws IOException, JAXBException {
            for (String fileType : List.of("blob", "ref")) {
                try (InputStream in =
                        JAXBTest.class.getResourceAsStream(String.format("/soapenv-exchange-%s.xsd.xml", fileType))) {
                    byte[] xml = Objects.requireNonNull(in).readAllBytes();
                    samples.put(fileType, xml);
                }
            }

            context = JAXBContext.newInstance("ru.otus.exchange.xsdschema");
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void xmlBench(ExecutionPlan plan, Blackhole bh) throws JAXBException {
        Unmarshaller unmarshaller = plan.context.createUnmarshaller();

        String type = plan.type;

        byte[] xml = Objects.requireNonNull(plan.samples.get(type));
        InputStream is = new ByteArrayInputStream(xml);
        Source ss = new StreamSource(is);

        bh.consume(unmarshaller.unmarshal(ss, EnvelopeType.class).getValue());
    }
}
