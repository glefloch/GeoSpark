/**
 * FILE: PolygonRDDTest.java
 * PATH: org.datasyslab.geospark.spatialRDD.PolygonRDDTest.java
 * Copyright (c) 2015-2017 GeoSpark Development Team
 * All rights reserved.
 */
package org.datasyslab.geospark.spatialRDD;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.datasyslab.geospark.enums.FileDataSplitter;
import org.datasyslab.geospark.enums.GridType;
import org.datasyslab.geospark.enums.IndexType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Arizona State University DataSystems Lab
 *
 */

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import com.vividsolutions.jts.index.strtree.STRtree;

// TODO: Auto-generated Javadoc
/**
 * The Class PolygonRDDTest.
 */
public class PolygonRDDTest {
    
    /** The sc. */
    public static JavaSparkContext sc;
    
    /** The prop. */
    static Properties prop;
    
    /** The input. */
    static InputStream input;
    
    /** The Input location. */
    static String InputLocation;
    
    static String InputLocationGeojson;
    
    /** The offset. */
    static Integer offset;
    
    /** The splitter. */
    static FileDataSplitter splitter;
    
    /** The grid type. */
    static GridType gridType;
    
    /** The index type. */
    static IndexType indexType;
    
    /** The num partitions. */
    static Integer numPartitions;
    
    /**
     * Once executed before all.
     */
    @BeforeClass
    public static void onceExecutedBeforeAll() {
        SparkConf conf = new SparkConf().setAppName("PolygonTest").setMaster("local[2]");
        sc = new JavaSparkContext(conf);
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        prop = new Properties();
        input = PolygonRDDTest.class.getClassLoader().getResourceAsStream("polygon.test.properties");
        InputLocation = "file://"+PolygonRDDTest.class.getClassLoader().getResource("primaryroads-polygon.csv").getPath();
        offset = 0;
        splitter = null;
        gridType = null;
        indexType = null;
        numPartitions = 0;

        try {
            // load a properties file
            prop.load(input);
            //InputLocation = prop.getProperty("inputLocation");
            InputLocation = "file://"+PolygonRDDTest.class.getClassLoader().getResource(prop.getProperty("inputLocation")).getPath();
            InputLocationGeojson = "file://"+PolygonRDDTest.class.getClassLoader().getResource(prop.getProperty("inputLocationGeojson")).getPath();
            offset = Integer.parseInt(prop.getProperty("offset"));
            splitter = FileDataSplitter.getFileDataSplitter(prop.getProperty("splitter"));
            gridType = GridType.getGridType(prop.getProperty("gridType"));
            indexType = IndexType.getIndexType(prop.getProperty("indexType"));
            numPartitions = Integer.parseInt(prop.getProperty("numPartitions"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Test constructor.
     *
     * @throws Exception the exception
     */
    /*
        This test case will load a sample data file and
     */
    @Test
    public void testConstructor() throws Exception {
        PolygonRDD spatialRDD = new PolygonRDD(sc, InputLocation, splitter, true, numPartitions,StorageLevel.MEMORY_ONLY());
        //todo: Set this to debug level
        assert spatialRDD.approximateTotalCount>=1;
        assert spatialRDD.boundaryEnvelope!=null;
    }

    @Test
    public void testGeoJSONConstructor() throws Exception {
        PolygonRDD spatialRDD = new PolygonRDD(sc, InputLocationGeojson, FileDataSplitter.GEOJSON, true, 4,StorageLevel.MEMORY_ONLY());
        //todo: Set this to debug level
        assert spatialRDD.approximateTotalCount==1001;
        assert spatialRDD.boundaryEnvelope!=null;
    }
    
    /**
     * Test hilbert curve spatial partitioing.
     *
     * @throws Exception the exception
     */
    /*
     *  This test case test whether the Hilbert Curve grid can be build correctly.
     */
    @Test
    public void testHilbertCurveSpatialPartitioing() throws Exception {
    	PolygonRDD spatialRDD = new PolygonRDD(sc, InputLocation, splitter, true, 10,StorageLevel.MEMORY_ONLY());
        spatialRDD.spatialPartitioning(GridType.HILBERT);
        for (Envelope d : spatialRDD.grids) {
        	//System.out.println("PointRDD spatial partitioning grids: "+d.grid);
        }
    }
    
    /**
     * Test R tree spatial partitioing.
     *
     * @throws Exception the exception
     */
    /*
     *  This test case test whether the STR-Tree grid can be build correctly.
     */
    @Test
    public void testRTreeSpatialPartitioing() throws Exception {
    	PolygonRDD spatialRDD = new PolygonRDD(sc, InputLocation, splitter, true, 10,StorageLevel.MEMORY_ONLY());
        spatialRDD.spatialPartitioning(GridType.RTREE);
        for (Envelope d : spatialRDD.grids) {
        	//System.out.println("PointRDD spatial partitioning grids: "+d.grid);
        }

    }
    
    /**
     * Test voronoi spatial partitioing.
     *
     * @throws Exception the exception
     */
    /*
     *  This test case test whether the Voronoi grid can be build correctly.
     */
    @Test
    public void testVoronoiSpatialPartitioing() throws Exception {
    	PolygonRDD spatialRDD = new PolygonRDD(sc, InputLocation, splitter, true, 10,StorageLevel.MEMORY_ONLY());
        spatialRDD.spatialPartitioning(GridType.VORONOI);
        for (Envelope d : spatialRDD.grids) {
        	//System.out.println("PointRDD spatial partitioning grids: "+d.grid);
        }

    }

    
    /**
     * Test build index without set grid.
     *
     * @throws Exception the exception
     */
    @Test
    public void testBuildIndexWithoutSetGrid() throws Exception {
    	PolygonRDD spatialRDD = new PolygonRDD(sc, InputLocation, splitter, true, numPartitions,StorageLevel.MEMORY_ONLY());
        spatialRDD.buildIndex(IndexType.RTREE,false);
    }


    /**
     * Test build rtree index.
     *
     * @throws Exception the exception
     */
    @Test
    public void testBuildRtreeIndex() throws Exception {
    	PolygonRDD spatialRDD = new PolygonRDD(sc, InputLocation, splitter, true, numPartitions,StorageLevel.MEMORY_ONLY());
        spatialRDD.spatialPartitioning(gridType);
        spatialRDD.buildIndex(IndexType.RTREE,true);
        if(spatialRDD.indexedRDD.take(1).get(0) instanceof STRtree)
        {
            List<Polygon> result = ((STRtree) spatialRDD.indexedRDD.take(1).get(0)).query(spatialRDD.boundaryEnvelope);
        }
        else
        {
            List<Polygon> result = ((Quadtree) spatialRDD.indexedRDD.take(1).get(0)).query(spatialRDD.boundaryEnvelope);

        }
    }
    
    /**
     * Test build quadtree index.
     *
     * @throws Exception the exception
     */
    @Test
    public void testBuildQuadtreeIndex() throws Exception {
    	PolygonRDD spatialRDD = new PolygonRDD(sc, InputLocation, splitter, true, numPartitions,StorageLevel.MEMORY_ONLY());
        spatialRDD.spatialPartitioning(gridType);
        spatialRDD.buildIndex(IndexType.QUADTREE,true);
        if(spatialRDD.indexedRDD.take(1).get(0) instanceof STRtree)
        {
            List<Polygon> result = ((STRtree) spatialRDD.indexedRDD.take(1).get(0)).query(spatialRDD.boundaryEnvelope);
        }
        else
        {
            List<Polygon> result = ((Quadtree) spatialRDD.indexedRDD.take(1).get(0)).query(spatialRDD.boundaryEnvelope);

        }
    }
    
    /**
     * Test MBR.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMBR() throws Exception {
    	PolygonRDD polygonRDD = new PolygonRDD(sc, InputLocation, splitter, true, numPartitions,StorageLevel.MEMORY_ONLY());
    	RectangleRDD rectangleRDD=polygonRDD.MinimumBoundingRectangle();
    	List<Object> result = rectangleRDD.rawSpatialRDD.collect();
    	assert result.size()>-1;
    }  
    
    /*
    @Test
    public void testPolygonUnion()
    {
    	PolygonRDD polygonRDD = new PolygonRDD(sc, InputLocation, offset, splitter, numPartitions);
    	assert polygonRDD.PolygonUnion() instanceof Polygon;
    }
    */

    /**
     * Tear down.
     */
    @AfterClass
    public static void TearDown() {
        sc.stop();
    }
}
