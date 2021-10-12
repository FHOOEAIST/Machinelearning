/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.core.mapping;

import science.aist.machinelearning.core.ProblemGene;

/**
 * Interface for the implementation of geneCreators. GeneCreators create genes depending on the implementation and use
 * the given problemGene.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public interface GeneCreator<ST, PT> {

    ST createGene(ProblemGene<PT> problem);
}
