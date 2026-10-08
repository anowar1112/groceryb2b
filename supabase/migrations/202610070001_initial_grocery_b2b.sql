-- Run this file once in Supabase Dashboard > SQL Editor.
-- The app's publishable key is intentionally safe to distribute; these RLS
-- policies, rather than a secret key in the Android app, protect user data.

create extension if not exists pgcrypto;

create table public.categories (
    id text primary key,
    name_bn text not null,
    name_en text not null,
    sort_order integer not null unique check (sort_order > 0)
);

create table public.products (
    id uuid primary key default gen_random_uuid(),
    category_id text not null references public.categories(id),
    name_bn text not null,
    name_en text not null,
    brand text not null,
    unit text not null,
    price integer not null check (price >= 0),
    discount_percent integer not null default 0 check (discount_percent between 0 and 100),
    stock integer not null default 0 check (stock >= 0),
    minimum_order_quantity integer not null default 1 check (minimum_order_quantity > 0),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table public.shops (
    id uuid primary key default gen_random_uuid(),
    auth_user_id uuid not null unique default auth.uid() references auth.users(id) on delete cascade,
    shop_name text not null,
    owner_name text not null,
    mobile_number text not null unique,
    address text not null,
    delivery_location text not null,
    landmark text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table public.orders (
    id uuid primary key default gen_random_uuid(),
    shop_id uuid not null references public.shops(id),
    total_price integer not null check (total_price >= 0),
    status text not null default 'PENDING' check (status in ('PENDING', 'CONFIRMED', 'DELIVERED', 'CANCELLED')),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table public.order_items (
    id uuid primary key default gen_random_uuid(),
    order_id uuid not null references public.orders(id) on delete cascade,
    product_id uuid references public.products(id) on delete set null,
    product_name_bn text not null,
    product_name_en text not null,
    quantity integer not null check (quantity > 0),
    price_per_unit integer not null check (price_per_unit >= 0),
    total_price integer not null check (total_price >= 0)
);

create index products_category_id_idx on public.products(category_id);
create index orders_shop_id_idx on public.orders(shop_id, created_at desc);
create index order_items_order_id_idx on public.order_items(order_id);

create or replace function public.set_updated_at()
returns trigger language plpgsql security invoker set search_path = '' as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

create trigger products_set_updated_at before update on public.products
for each row execute function public.set_updated_at();
create trigger shops_set_updated_at before update on public.shops
for each row execute function public.set_updated_at();
create trigger orders_set_updated_at before update on public.orders
for each row execute function public.set_updated_at();

alter table public.categories enable row level security;
alter table public.products enable row level security;
alter table public.shops enable row level security;
alter table public.orders enable row level security;
alter table public.order_items enable row level security;

-- Anyone can read the product catalog. Product administration is deliberately
-- server-side only until an authenticated admin role is introduced.
create policy "catalog is readable" on public.categories for select using (true);
create policy "products are readable" on public.products for select using (true);

create policy "users create their own shop" on public.shops
for insert to authenticated with check (auth_user_id = auth.uid());
create policy "users read their own shop" on public.shops
for select to authenticated using (auth_user_id = auth.uid());
create policy "users update their own shop" on public.shops
for update to authenticated using (auth_user_id = auth.uid()) with check (auth_user_id = auth.uid());

create policy "users create orders for their shop" on public.orders
for insert to authenticated with check (
    exists (select 1 from public.shops where shops.id = shop_id and shops.auth_user_id = auth.uid())
);
create policy "users read their shop orders" on public.orders
for select to authenticated using (
    exists (select 1 from public.shops where shops.id = shop_id and shops.auth_user_id = auth.uid())
);

create policy "users create their order items" on public.order_items
for insert to authenticated with check (
    exists (
        select 1 from public.orders
        join public.shops on shops.id = orders.shop_id
        where orders.id = order_id and shops.auth_user_id = auth.uid()
    )
);
create policy "users read their order items" on public.order_items
for select to authenticated using (
    exists (
        select 1 from public.orders
        join public.shops on shops.id = orders.shop_id
        where orders.id = order_items.order_id and shops.auth_user_id = auth.uid()
    )
);

insert into public.categories (id, name_bn, name_en, sort_order) values
    ('rice', 'চাল', 'Rice', 1),
    ('dal', 'ডাল', 'Dal', 2),
    ('oil', 'তেল', 'Oil', 3),
    ('salt_sugar', 'লবণ ও চিনি', 'Salt & Sugar', 4),
    ('biscuits', 'বিস্কুট', 'Biscuits', 5),
    ('noodles', 'নুডলস', 'Noodles', 6),
    ('drinks', 'পানীয়', 'Drinks', 7),
    ('soap', 'সাবান', 'Soap', 8),
    ('shampoo', 'শ্যাম্পু', 'Shampoo', 9),
    ('detergent', 'ডিটারজেন্ট', 'Detergent', 10),
    ('tissue', 'টিস্যু', 'Tissue', 11),
    ('spices', 'মসলা', 'Spices', 12),
    ('other', 'অন্যান্য', 'Other grocery products', 13)
on conflict (id) do update set
    name_bn = excluded.name_bn,
    name_en = excluded.name_en,
    sort_order = excluded.sort_order;
