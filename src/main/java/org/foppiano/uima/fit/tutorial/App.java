package org.foppiano.uima.fit.tutorial;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.metadata.SofaMapping;
import org.apache.uima.annotator.WhitespaceTokenizer;
import org.apache.uima.annotator.dict_annot.impl.DictionaryAnnotator;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.SofaMappingFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.foppiano.uima.fit.tutorial.annotator.SimpleParserAE;
import org.foppiano.uima.fit.tutorial.casConsumer.SimpleCC;
import org.foppiano.uima.fit.tutorial.collectorReader.SimpleCR;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws UIMAException, IOException {

        //new PathMatchingResourcePatternResolver().getResources("classpath*:META-INF/org.apache.uima.fit/types.txt");

        //TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription();
        //TypeDescription[] types = tsd.getTypes();

        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                SimpleCR.class,
                SimpleCR.PARAM_SOURCE_FILE,
                "/home/lf84914/development/workspace/uimaFitTestTutorial/src/main/resources/sampleInput.csv");

        AnalysisEngineDescription preparationEngine = AnalysisEngineFactory.createEngineDescription(SimpleParserAE.class);

        AnalysisEngineDescription dictionaryEngine = AnalysisEngineFactory.createEngineDescription(
                DictionaryAnnotator.class,
                "DictionaryFiles",
                new String[]{
                        "dictionary.xml"
                },
                "InputMatchType",
                "org.apache.uima.TokenAnnotation"
        );

        /*try {
            dictionaryEngine.toXML(new FileWriter(new File("/tmp/bao")));
        } catch (SAXException e) {

        }*/

/*        SofaMapping mapper = SofaMappingFactory.createSofaMapping(dictionaryEngine.getClass().getCanonicalName(), "textOnly", "dictionary");
        dictionaryEngine.setSofaMappings(new SofaMapping[]{mapper});*/

        AnalysisEngineDescription whitespaceEngine = AnalysisEngineFactory.createEngineDescription(WhitespaceTokenizer.class,
                "SofaNames",
                new String[]{SimpleParserAE.SOFA_NAME_TEXT_ONLY}
        );

        AnalysisEngineDescription casConsumer = AnalysisEngineFactory.createEngineDescription(
                SimpleCC.class,
                SimpleCC.PARAM_OUTPUT_DIR,
                "/home/lf84914/development/epo/apl/data/out"
        );

        AggregateBuilder builder = new AggregateBuilder();
        builder.add(preparationEngine);
        builder.add(whitespaceEngine);
        builder.add(dictionaryEngine, SimpleParserAE.SOFA_NAME_TEXT_ONLY, SimpleParserAE.SOFA_NAME_TEXT_ONLY);
        //builder.addSofaMapping(DictionaryAnnotator.class.getName(), "textOnly", "dictionary");


  //      SimplePipeline.runPipeline(reader, preparationEngine, whitespaceEngine, dictionaryEngine, casConsumer);
        SimplePipeline.runPipeline(reader, builder.createAggregateDescription(), casConsumer);
    }
}
