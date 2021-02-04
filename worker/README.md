# Worker

Collection of worker utilities.

## Job Executor

JobExecutor is a simple utility that takes a list of jobs and completes them on the given execution context.

### Usage

```scala
val jobs = List.fill(10) {
  Job(
    //Whatever your function is
    () => Future.successful[Boolean](true)
  )
}
val executor = new JobExecutor[Boolean](jobs)

//Jobs will start off in a pending state
assert(executor.pending.size == 10)

//Start execution
executor.execute()

//Jobs should all be in a completed state eventually
eventually {
  assert(executor.pending.isEmpty)
  assert(executor.inProgress.isEmpty)
  assert(executor.failed.isEmpty)
  assert(executor.completed.size == 10)
  assert(executor.isDone)
}
```