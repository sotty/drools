package org.drools.factmodel.traits;

/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class LogicalMapTraitTest {

    @Test
    public void testTraitMismatchTypes()
    {
        String drl = "" +
                "package openehr.test;//org.drools.factmodel.traits;\n" +
                "\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "import org.drools.factmodel.traits.Trait;\n" +
                "import org.drools.factmodel.traits.Alias;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "//declare org.drools.factmodel.MapCore\n" +
                "//end\n" +
                "\n" +
                "\n" +
                "declare Parent\n" +
                "@Traitable( logical = true )\n" +
                "@propertyReactive\n" +
                "    name : String\n" +
                "    id : int\n" +
                "end\n" +
                "\n" +
                "declare trait ParentTrait\n" +
                "@Trait( logical = true )" + //does not have effect
                "@propertyReactive\n" +
                "    name : String\n" +
                "    id : float\n" +   //different exception for Float
                "end\n" +
                "\n" +
                "rule \"init\"\n" +
                "when\n" +
                "then\n" +
                "    Parent p = new Parent(\"papa\", 1010);\n" +
                "    insert( p );\n" +
                "end\n" +
                "\n" +
                "rule \"don\"\n" +
                "when\n" +
                "    $p : Parent(id > 1000)\n" +
                "then\n" +
                "    don( $p , ParentTrait.class );\n" +
                "    list.add(\"correct\");\n" +
                "end";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());

        List list = new ArrayList();
        ksession.setGlobal("list",list);
        ksession.fireAllRules();

        assertTrue(list.contains("correct"));
    }

    @Test
    public void testMapTraitsMismatchTypes()
    {
        String drl = "" +
                "package openehr.test;//org.drools.factmodel.traits;\n" +
                "\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "import org.drools.factmodel.traits.Trait;\n" +
                "import org.drools.factmodel.traits.Alias;\n" +
                "import java.util.*;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare org.drools.factmodel.MapCore\n" +
                "@Traitable( logical = true )\n" +
                "end\n" +
                "" +
                "declare HashMap @Traitable end \n" +
                "\n" +
                "\n" +
                "declare trait ParentTrait\n" +
                "//@Trait( logical = true )" +
                "@propertyReactive\n" +
                "    name : String\n" +
                "    id : int\n" +
                "end\n" +
                "\n" +
                "declare trait ChildTrait\n" +
                "//@Trait( logical = true )" +
                "@propertyReactive\n" +
                "    naam : String\n" +
                "    id : int\n" + //even when they are the same type
                "end\n" +
                "\n" +
                "rule \"don1\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $map : Map()\n" +
                "then\n" +
                "    don( $map , ParentTrait.class );\n" +
                "    don( $map , ChildTrait.class );\n" +
                "    list.add(\"correct\");\n" +
                "end";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());

        List list = new ArrayList();
        ksession.setGlobal("list",list);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name","hulu");
        ksession.insert(map);
        ksession.fireAllRules();

        assertTrue(list.contains("correct"));
    }

    @Test
    public void testTraitNoType()
    {
        String drl = "" +
                "package openehr.test;//org.drools.factmodel.traits;\n" +
                "\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "import org.drools.factmodel.traits.Trait;\n" +
                "import org.drools.factmodel.traits.Alias;\n" +
                "import java.util.*;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "\n" +
                "declare Parent\n" +
                "@Traitable( logical = true )" +
                "@propertyReactive\n" +
                "end\n" +
                "\n" +
                "declare trait ChildTrait\n" +
                "//@Trait( logical = true )" +
                "@propertyReactive\n" +
                "    naam : String = \"kudak\"\n" +
                "    id : int = 1020\n" +
                "end\n" +
                "\n" +
                "rule \"don\"\n" +
                "no-loop\n" +
                "when\n" +
                "then\n" +
                "    Parent p = new Parent();" +
                "    insert(p);\n" +
                "    don( p , ChildTrait.class );\n" +
                "    list.add(\"correct1\");\n" +
                "end\n" +
                "\n" +
                "rule \"check\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $c : ChildTrait($n : naam == \"kudak\", id == 1020 )\n" +
                "    $p : Parent( fields[\"naam\"] == $n )\n" +
                "then\n" +
                "    System.out.println($p);\n" +
                "    System.out.println($c);\n" +
                "    list.add(\"correct2\");\n" +
                "end";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());

        List list = new ArrayList();
        ksession.setGlobal("list",list);
        ksession.fireAllRules();

        assertTrue(list.contains("correct1"));
        assertTrue(list.contains("correct2"));
    }

    @Test
    public void testMapTraitNoType()
    {
        String drl = "" +
                "package openehr.test;//org.drools.factmodel.traits;\n" +
                "\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "import org.drools.factmodel.traits.Trait;\n" +
                "import org.drools.factmodel.traits.Alias;\n" +
                "import java.util.*;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare org.drools.factmodel.MapCore\n" +
                "//@Traitable( logical = true )\n" +
                "end\n" +
                "" +
                "declare HashMap @Traitable end \n" +
                "\n" +
                "\n" +
                "declare trait ChildTrait\n" +
                "//@Trait( logical = true )" +
                "@propertyReactive\n" +
                "    naam : String = \"kudak\"\n" +
                "    id : int = 1020\n" +
                "end\n" +
                "\n" +
                "rule \"don\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $map : Map()" +    //map is empty
                "then\n" +
                "    don( $map , ChildTrait.class );\n" +
                "    list.add(\"correct1\");\n" +
                "end\n" +
                "\n" +
                "rule \"check\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $c : ChildTrait($n : naam == \"kudak\", id == 1020 )\n" +
                "    $p : Map( this[\"naam\"] == $n )\n" +
                "then\n" +
                "    System.out.println($p);\n" +
                "    System.out.println($c);\n" +
                "    list.add(\"correct2\");\n" +
                "end";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());

        List list = new ArrayList();
        ksession.setGlobal("list",list);
        Map<String,Object> map = new HashMap<String, Object>();
//        map.put("name", "hulu");
        ksession.insert(map);
        ksession.fireAllRules();

        assertTrue(list.contains("correct1"));
        assertTrue(list.contains("correct2"));
    }

    @Test
    public void testMapTraitMismatchTypes()
    {
        String drl = "" +
                "package openehr.test;//org.drools.factmodel.traits;\n" +
                "\n" +
                "import org.drools.factmodel.traits.Traitable;\n" +
                "import org.drools.factmodel.traits.Trait;\n" +
                "import org.drools.factmodel.traits.Alias;\n" +
                "import java.util.*;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare org.drools.factmodel.MapCore\n" +
                "@Traitable( logical = true )\n" +  //does not have effect
                "end\n" +
                "" +
                "declare HashMap @Traitable end \n" +
                "\n" +
                "\n" +
                "declare trait ChildTrait\n" +
                "@Trait( logical = true )" +
                "@propertyReactive\n" +
                "    naam : String = \"kudak\"\n" +
                "    id : int = 1020\n" +
                "end\n" +
                "\n" +
                "rule \"don\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $map : Map()" +
                "then\n" +
                "    don( $map , ChildTrait.class );\n" +
                "    list.add(\"correct1\");\n" +
                "end\n" +
                "\n" +
                "rule \"check\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $c : ChildTrait($n : naam == \"kudak\", id == 1020 )\n" +
                "    $p : Map( this[\"naam\"] == 12 )\n" +
                "then\n" +
                "    System.out.println($p);\n" +
                "    System.out.println($c);\n" +
                "    list.add(\"correct2\");\n" +
                "end";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();
        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());

        List list = new ArrayList();
        ksession.setGlobal("list",list);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("naam", new Integer(12) );
        ksession.insert(map);
        ksession.fireAllRules();

        assertTrue(list.contains("correct1"));
        assertTrue(list.contains("correct2"));
    }

    private KnowledgeBase buildKB( String drlPath ) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newClassPathResource(drlPath), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        return knowledgeBase;
    }

    private KnowledgeBase loadKnowledgeBaseFromString( String drlSource ){
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource(drlSource.getBytes()), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            fail( knowledgeBuilder.getErrors().toString() );
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        return knowledgeBase;

    }


}
