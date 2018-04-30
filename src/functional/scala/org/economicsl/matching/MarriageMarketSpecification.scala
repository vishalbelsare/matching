/*
Copyright 2018 EconomicSL

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.economicsl.matching

import org.scalacheck.{Gen, Properties}


object MarriageMarketSpecification extends Properties("marriage-market") {

  import org.scalacheck.Prop._

  val man: Gen[Man] = {
    for {
      id <- Gen.posNum[Long]
      quality <- Gen.posNum[Long]
    } yield Man(id, quality, Man.womanByQuality)
  }

  val woman: Gen[Woman] = {
    for {
      id <- Gen.posNum[Long]
      quality <- Gen.posNum[Long]
    } yield Woman(id, quality, Woman.manByQuality)
  }

  def setOfN[T](n: Int, g: Gen[T]): Gen[Set[T]] = {
    Gen.containerOfN[Set, T](n, g)
  }

  def unMatched: Gen[(Set[Man], Set[Woman])] = Gen.sized {
    n => setOfN(n, man).flatMap(ms => setOfN(ms.size, woman).map(ws => (ms, ws)))
      .suchThat { case (ms, ws) => ms.size == ws.size }
  }

  property("all men and women are matched") = forAll(unMatched) {
    case (ms, ws) =>
      val (unMatchedMs, unMatchedWs, matching) = DeferredAcceptance.weaklyStableMatching(ms, ws)
      unMatchedMs.isEmpty && unMatchedWs.isEmpty && (matching.size == ms.size)
  }

}
