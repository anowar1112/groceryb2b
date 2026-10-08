-- Add stable keys so retries after partial uploads update existing rows.
alter table public.orders add column if not exists client_id text;
update public.orders set client_id = id::text where client_id is null;
create unique index if not exists orders_client_id_uidx on public.orders(client_id);

alter table public.order_items add column if not exists client_id text;
update public.order_items
set client_id = order_id::text || ':' || id::text
where client_id is null;
create unique index if not exists order_items_client_id_uidx on public.order_items(client_id);

-- Admin membership is provisioned out of band; app-side admin flags are not trusted by RLS.
create table if not exists public.admin_users (
    user_id uuid primary key references auth.users(id) on delete cascade
);
alter table public.admin_users enable row level security;
revoke all on public.admin_users from anon, authenticated;

create or replace function public.is_admin()
returns boolean
language sql
stable
security definer
set search_path = ''
as $$
    select exists (
        select 1 from public.admin_users where user_id = auth.uid()
    );
$$;
revoke all on function public.is_admin() from public, anon;
grant execute on function public.is_admin() to authenticated;

create policy "admins read all orders" on public.orders
for select to authenticated using (public.is_admin());
create policy "admins update orders" on public.orders
for update to authenticated
using (public.is_admin())
with check (public.is_admin());
create policy "admins read all order items" on public.order_items
for select to authenticated using (public.is_admin());

-- After an admin account is created, provision its auth.users UUID from the
-- Supabase SQL editor, for example:
-- insert into public.admin_users(user_id)
-- select id from auth.users where email = '<trusted-admin-email>';