/*
 * Copyright 2015-2016 Magnus Madsen, Ming-Ho Yee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.uwaterloo.flix.api

import java.util

import ca.uwaterloo.flix.runtime.Value

import scala.collection.immutable

final class WrappedValue(val ref: AnyRef) extends IValue {

  def isUnit: Boolean = ref match {
    case Value.Unit => true
    case _ => false
  }

  def getType: IType = throw new UnsupportedOperationException("Not yet implemented. Sorry.") // TODO

  def getBool: Boolean = Value.cast2bool(ref)

  def isTrue: Boolean = getBool

  def isFalse: Boolean = !getBool

  def getChar: Char = Value.cast2char(ref)

  def getFloat32: Float = Value.cast2float32(ref)

  def getFloat64: Double = Value.cast2float64(ref)

  def getInt8: Byte = Value.cast2int8(ref)

  def getInt16: Short = Value.cast2int16(ref)

  def getInt32: Int = Value.cast2int32(ref)

  def getInt64: Long = Value.cast2int64(ref)

  def getBigInt: java.math.BigInteger = Value.cast2bigInt(ref)

  def getStr: String = Value.cast2str(ref)

  def getTuple: Array[IValue] = Value.cast2tuple(ref).map(e => new WrappedValue(e))

  def getTagName: String = Value.cast2tag(ref).tag

  def getTagValue: IValue = new WrappedValue(Value.cast2tag(ref).value)

  def getNativeRef: AnyRef = ref

  def getJavaOpt: java.util.Optional[IValue] = Value.cast2opt(ref) match {
    case null => java.util.Optional.empty()
    case o => java.util.Optional.of(new WrappedValue(o))
  }

  def getScalaOpt: scala.Option[IValue] = Value.cast2opt(ref) match {
    case null => scala.None
    case o => scala.Some(new WrappedValue(o))
  }

  def getJavaList: java.util.List[IValue] = {
    val xs = Value.cast2list(ref)
    val r = new java.util.LinkedList[IValue]
    for (x <- xs) {
      r.add(new WrappedValue(x))
    }
    r
  }

  def getScalaList: immutable.List[IValue] = Value.cast2list(ref).map(e => new WrappedValue(e))

  def getJavaSet: java.util.Set[IValue] = {
    val xs = Value.cast2set(ref)
    val r = new util.HashSet[IValue]()
    for (x <- xs) {
      r.add(new WrappedValue(x))
    }
    r
  }

  def getScalaSet: immutable.Set[IValue] = Value.cast2set(ref).map(e => new WrappedValue(e)).toSet

  def getJavaMap: java.util.Map[IValue, IValue] = {
    val xs = Value.cast2map(ref)
    val r = new java.util.HashMap[IValue, IValue]
    for ((k, v) <- xs) {
      r.put(new WrappedValue(k), new WrappedValue(v))
    }
    r
  }

  def getScalaMap: immutable.Map[IValue, IValue] = Value.cast2map(ref).foldLeft(Map.empty[IValue, IValue]) {
    case (macc, (k, v)) => macc + (new WrappedValue(k) -> new WrappedValue(v))
  }

  def getUnsafeRef: AnyRef = ref

  override def equals(other: Any): Boolean = other match {
    case that: WrappedValue => ref == that.ref
    case _ => false
  }

  override def hashCode(): Int = ref.hashCode()

}
