// Copyright 2024 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.devtools.build.lib.runtime;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.devtools.build.lib.buildeventstream.BuildEventArtifactUploader;
import com.google.devtools.build.lib.vfs.Path;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.function.Supplier;
import javax.annotation.Nullable;

/** Creates different types of {@link InstrumentationOutputBuilder}. */
public final class InstrumentationOutputFactory {
  private final Supplier<LocalInstrumentationOutput.Builder>
      localInstrumentationOutputBuilderSupplier;

  private final Supplier<BuildEventArtifactInstrumentationOutput.Builder>
      buildEventArtifactInstrumentationOutputBuilderSupplier;

  private InstrumentationOutputFactory(
      Supplier<LocalInstrumentationOutput.Builder> localInstrumentationOutputBuilderSupplier,
      Supplier<BuildEventArtifactInstrumentationOutput.Builder>
          buildEventArtifactInstrumentationOutputBuilderSupplier) {
    this.localInstrumentationOutputBuilderSupplier = localInstrumentationOutputBuilderSupplier;
    this.buildEventArtifactInstrumentationOutputBuilderSupplier =
        buildEventArtifactInstrumentationOutputBuilderSupplier;
  }

  /**
   * Creates {@link LocalInstrumentationOutput} with given parameters.
   *
   * <p>{@code name} and {@code path} are required since they indicate what the output is and where
   * it is stored.
   *
   * <p>When the name of the instrumentation output is complicated, an optional {@code
   * convenienceName} parameter can be passed in so that a symlink pointing to the output with such
   * a simpler name is created. See {@link LocalInstrumentationOutput.Builder#setConvenienceName}.
   *
   * <p>User can also pass in the optional {@code append} and {@code internal} {@code Boolean}s to
   * control how {@code path} creates the {@link OutputStream}. See {@link
   * LocalInstrumentationOutput#createOutputStream()} for more details.
   */
  public LocalInstrumentationOutput createLocalInstrumentationOutput(
      String name,
      Path path,
      @Nullable String convenienceName,
      @Nullable Boolean append,
      @Nullable Boolean internal) {
    return localInstrumentationOutputBuilderSupplier
        .get()
        .setName(name)
        .setPath(path)
        .setConvenienceName(convenienceName)
        .setAppend(append)
        .setInternal(internal)
        .build();
  }

  public BuildEventArtifactInstrumentationOutput createBuildEventArtifactInstrumentationOutput(
      String name, BuildEventArtifactUploader uploader) {
    return buildEventArtifactInstrumentationOutputBuilderSupplier
        .get()
        .setName(name)
        .setUploader(uploader)
        .build();
  }

  /** Builder for {@link InstrumentationOutputFactory}. */
  public static class Builder {
    @Nullable
    private Supplier<LocalInstrumentationOutput.Builder> localInstrumentationOutputBuilderSupplier;

    @Nullable
    private Supplier<BuildEventArtifactInstrumentationOutput.Builder>
        buildEventArtifactInstrumentationOutputBuilderSupplier;

    @CanIgnoreReturnValue
    public Builder setLocalInstrumentationOutputBuilderSupplier(
        Supplier<LocalInstrumentationOutput.Builder> localInstrumentationOutputBuilderSupplier) {
      this.localInstrumentationOutputBuilderSupplier = localInstrumentationOutputBuilderSupplier;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder setBuildEventArtifactInstrumentationOutputBuilderSupplier(
        Supplier<BuildEventArtifactInstrumentationOutput.Builder>
            buildEventArtifactInstrumentationOutputBuilderSupplier) {
      this.buildEventArtifactInstrumentationOutputBuilderSupplier =
          buildEventArtifactInstrumentationOutputBuilderSupplier;
      return this;
    }

    public InstrumentationOutputFactory build() {
      return new InstrumentationOutputFactory(
          checkNotNull(
              localInstrumentationOutputBuilderSupplier,
              "Cannot create InstrumentationOutputFactory without localOutputBuilderSupplier"),
          checkNotNull(
              buildEventArtifactInstrumentationOutputBuilderSupplier,
              "Cannot create InstrumentationOutputFactory without bepOutputBuilderSupplier"));
    }
  }
}