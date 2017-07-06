/*
 * Grakn - A Distributed Semantic Database
 * Copyright (C) 2016  Grakn Labs Limited
 *
 * Grakn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grakn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Grakn. If not, see <http://www.gnu.org/licenses/gpl.txt>.
 *
 */

package ai.grakn.graql.internal.gremlin;

import ai.grakn.graql.Graql;
import ai.grakn.graql.Var;
import ai.grakn.graql.internal.gremlin.fragment.Fragment;
import ai.grakn.graql.internal.gremlin.fragment.Fragments;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static ai.grakn.graql.internal.gremlin.fragment.Fragments.outIsa;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PlanTest {


    @Test
    public void planComplexityShouldAlwaysEqualCostFromGraqlTraversal() {
        Var a = Graql.var("a");
        Var x = Graql.var("x");
        Var y = Graql.var("y");
        Var z = Graql.var("z");

        Fragment outIsa = outIsa(null, y, a);
        outIsa.setEquivalentFragmentSet(mock(EquivalentFragmentSet.class));

        Fragment inShortcut = Fragments.inShortcut(null, y, Graql.var(), x, Optional.empty(), Optional.empty(), Optional.empty());
        Fragment outShortcut = Fragments.outShortcut(null, x, Graql.var(), z, Optional.empty(), Optional.empty(), Optional.empty());
        inShortcut.setEquivalentFragmentSet(mock(EquivalentFragmentSet.class));
        outShortcut.setEquivalentFragmentSet(mock(EquivalentFragmentSet.class));

        Plan plan = Plan.base();
        plan.tryPush(outIsa);
        plan.tryPush(inShortcut);
        plan.tryPush(outShortcut);

        List<Fragment> fragments = plan.fragments();

        double traversalComplexity = GraqlTraversal.create(ImmutableSet.of(fragments)).getComplexity();
        double planCost = plan.cost();

        assertEquals(traversalComplexity, planCost, 10.0);
    }
}